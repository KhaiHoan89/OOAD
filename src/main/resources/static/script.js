// 1. KIỂM TRA TRẠNG THÁI KHI VÀO TRANG
document.addEventListener("DOMContentLoaded", () => {
    checkLoginStatus();
});

async function checkLoginStatus() {
    try {
        const res = await fetch('/api/auth/current-user');
        if (res.ok) {
            const user = await res.json();
            showMainSection(user.userName);
        } else {
            showLoginSection();
        }
    } catch (err) {
        showLoginSection();
    }
}

function showMainSection(userName) {
    const loginSec = document.getElementById('login-section');
    const mainSec = document.getElementById('main-section');
    if (loginSec) loginSec.classList.add('hidden');
    if (mainSec) mainSec.classList.remove('hidden');

    const userDisplay = document.getElementById('user-display');
    if (userDisplay) userDisplay.innerText = userName;

    fetchAppointments();
}

function showLoginSection() {
    const loginSec = document.getElementById('login-section');
    const mainSec = document.getElementById('main-section');
    if (loginSec) loginSec.classList.remove('hidden');
    if (mainSec) mainSec.classList.add('hidden');
}

// 2. HIỂN THỊ DANH SÁCH LỊCH TRÌNH (TỔNG QUÁT)
async function fetchAppointments() {
    try {
        const res = await fetch('/api/appointments/all');
        if (res.ok) {
            const data = await res.json();
            const body = document.getElementById('app-body');
            if (!body) return;
            body.innerHTML = '';

            data.forEach(app => {
                // Lấy tên người dùng từ Backend trả về (không hard-code tên)
                let ownerDisplay = app.ownerName || `User ${app.userId}`;

                const startDate = new Date(app.start);
                const endDate = new Date(app.end);

                const formatTime = (date) => {
                    let h = date.getHours();
                    let m = date.getMinutes().toString().padStart(2, '0');
                    let ampm = h >= 12 ? 'CH' : 'SA';
                    h = h % 12 || 12;
                    return `${h.toString().padStart(2, '0')}:${m} ${ampm}`;
                };

                const dateStr = `${startDate.getDate()}/${startDate.getMonth() + 1}/${startDate.getFullYear()}`;

                body.innerHTML += `
                    <tr>
                        <td><b>${app.title}</b></td>
                        <td>
                            <div style="font-weight: bold; color: #0866ff;">
                                ${formatTime(startDate)} - ${formatTime(endDate)}
                            </div>
                            <div style="font-size: 12px; color: #65676b;">Ngày: ${dateStr}</div>
                        </td>
                        <td><span class="badge" style="background:#e4e6eb; padding:4px 8px; border-radius:4px;">${ownerDisplay}</span></td>
                    </tr>`;
            });
        }
    } catch (err) { console.error("Lỗi fetch danh sách:", err); }
}

// 3. LƯU CUỘC HẸN (SO SÁNH NĂM > THÁNG > NGÀY > GIỜ > PHÚT)
async function submitAppointment(option = null, joinId = null) {
    const titleIn = document.getElementById('title');
    const startIn = document.getElementById('start');
    const endIn = document.getElementById('end');

    if (!titleIn.value || !startIn.value || !endIn.value) {
        alert("⚠️ Vui lòng điền đủ thông tin!");
        return;
    }

    const startDT = new Date(startIn.value);
    const endDT = new Date(endIn.value);
    if (startDT >= endDT) {
        alert("❌ Sai rồi! Thời gian bắt đầu phải trước thời gian kết thúc.");
        return;
    }

    const bodyData = {
        title: titleIn.value.trim(),
        start: startIn.value + ":00",
        end: endIn.value + ":00"
    };

    let params = new URLSearchParams();
    if (option) params.append("option", option);
    if (joinId) params.append("joinId", joinId);

    try {
        const res = await fetch(`/api/appointments/submit?${params.toString()}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bodyData)
        });

        // XỬ LÝ CHỌN NHÓM ĐỂ GỘP (MÃ 303)
        // Trong hàm submitAppointment, phần xử lý mã lỗi 303 (Group Meeting)
        if (res.status === 303) {
            const others = await res.json();

            let message = `🔔 Phát hiện ${others.length} cuộc hẹn trùng giờ:\n\n`;
            others.forEach((app, index) => {
                const name = app.ownerName || `Người dùng ${app.userId}`;
                message += `${index + 1}. [${name}] - ${app.title}\n`;
            });
            message += `\nNhập STT để GỘP CHUNG, hoặc nhấn Cancel để tiếp tục kiểm tra lịch cá nhân của bạn.`;

            const userInput = prompt(message);

            if (userInput === null || userInput.trim() === "") {
                // Nếu nhấn Cancel: Gọi lại hàm mà không truyền joinId
                // Backend sẽ tự hiểu là không gộp nhóm và phải check Conflict cá nhân
                return submitAppointment(null, null);
            } else {
                const choice = parseInt(userInput) - 1;
                if (others[choice]) {
                    // Nếu chọn một STT: Gửi joinId để gộp nhóm
                    return submitAppointment(null, others[choice].appId);
                } else {
                    alert("STT không hợp lệ!");
                    return;
                }
            }
        }

        // XỬ LÝ CONFLICT (MÃ 409)
        else if (res.status === 409) {
            const msg = await res.text();
            if (confirm(`${msg}\n\nBạn có muốn GHI ĐÈ (Replace) không?`)) {
                return submitAppointment("Replace", joinId);
            }
            return;
        }

        else if (res.ok) {
            alert("✅ Thành công!");
            location.reload();
        }
    } catch (err) {
        alert("❌ Lỗi kết nối!");
    }
}
// 4. ĐĂNG NHẬP / ĐĂNG XUẤT
async function handleLogin() {
    const emailEl = document.getElementById('email');
    const passwordEl = document.getElementById('password');

    if (!emailEl || !passwordEl) return;

    const email = emailEl.value.trim();
    const password = passwordEl.value.trim();

    if (!email || !password) {
        alert("Vui lòng nhập email và mật khẩu!");
        return;
    }

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, password: password })
        });

        if (res.ok) {
            const user = await res.json();
            showMainSection(user.userName);
        } else {
            alert("❌ Sai tài khoản hoặc mật khẩu!");
        }
    } catch (err) {
        alert("❌ Lỗi kết nối!");
    }
}

async function handleLogout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    location.reload();
}