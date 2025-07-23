<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>대기 중</title>
    <script>
        (function() {
            // 쿠키에서 특정 이름의 값을 가져오는 유틸리티 함수
            const getCookie = (name) => {
                const value = `; ${document.cookie}`;
                const parts = value.split(`; ${name}=`);
                if (parts.length === 2) return parts.pop().split(';').shift();
            };

            // 대기열 상태를 폴링하는 함수
            const poll = () => {
                // 쿠키에서 X-GREENLIGHT-TOKEN을 가져옴
                const token = getCookie('X-GREENLIGHT-TOKEN');
                // URL 쿼리 파라미터에서 redirectUrl을 가져옴 (인터셉터에서 전달)
                const urlParams = new URLSearchParams(window.location.search);
                const redirectUrl = urlParams.get('redirectUrl');

                // Core-API의 check-or-enter API를 호출하여 대기열 상태 확인
                fetch('/api/v1/queue/check-or-enter?actionId=10001', {
                    headers: {
                        'X-GREENLIGHT-TOKEN': token // 쿠키에서 가져온 토큰을 헤더에 포함
                    }
                })
                .then(response => response.json())
                .then(data => {
                    // 응답 상태가 READY이면 원래 목적지로 이동
                    if (data.waitStatus === 'READY') {
                        if (redirectUrl) {
                            window.location.href = redirectUrl;
                        } else {
                            // redirectUrl이 없을 경우 기본 페이지로 이동 (예: 홈)
                            window.location.href = '/';
                        }
                    } else {
                        // READY가 아니면 2초 후 다시 폴링
                        setTimeout(poll, 2000);
                    }
                })
                .catch(error => {
                    console.error('폴링 중 오류 발생:', error);
                    // 오류 발생 시 5초 후 재시도
                    setTimeout(poll, 5000);
                });
            };

            // 페이지 로드 시 폴링 시작
            window.onload = poll;
        })();
    </script>
</head>
<body>
    <h1>대기 중입니다. 잠시만 기다려주세요.</h1>
</body>
</html>