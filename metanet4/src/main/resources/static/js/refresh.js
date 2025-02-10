async function fetchWithAuth(url, options = {}) {
    let response = await fetch(url, {
        ...options,
        credentials: "include"  // ✅ 쿠키 기반 요청 (Access Token 자동 전송)
    });

    // ✅ Access Token이 만료된 경우 (401 또는 403 응답)
    if (response.status === 401 || response.status === 403) {
        console.warn("🔴 Access Token이 만료됨. Refresh Token으로 재발급 요청");

        // ✅ Refresh Token을 이용해 Access Token 재발급 요청
        const refreshResponse = await fetch("/auth/refresh", {
            method: "POST",
            credentials: "include"
        });

        if (refreshResponse.ok) {
            console.log("🟢 새로운 Access Token 발급 완료");

            // ✅ 원래 요청을 다시 실행
            return fetch(url, {
                ...options,
                credentials: "include"
            });
        } else {
            console.warn("🔴 Refresh Token도 만료됨. 로그인 페이지로 이동");
            window.location.href = "/auth/login";
        }
    }

    return response;
}

// ✅ 10초마다 자동으로 로그인 상태 확인 (Access Token 만료 감지)
setInterval(async () => {
    let response = await fetchWithAuth("/auth/check");
    let data = await response.json();

    console.log("🔍 자동 로그인 상태 확인:", data);
    if (!data.loggedIn) {
        console.warn("🔴 로그인 만료됨. 로그인 페이지로 이동");
        window.location.href = "/auth/login";
    }
}, 1000*60*30);
