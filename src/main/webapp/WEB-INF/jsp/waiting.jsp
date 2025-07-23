<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>대기 중</title>
    <script>
        (function() {
            const getCookie = (name) => {
                const value = `; ${document.cookie}`;
                const parts = value.split(`; ${name}=`);
                if (parts.length === 2) return parts.pop().split(';').shift();
            };

            const poll = () => {
                const token = getCookie('X-GREENLIGHT-TOKEN');
                const urlParams = new URLSearchParams(window.location.search);
                const redirectUrl = urlParams.get('redirectUrl');

                fetch('/api/v1/queue/check-or-enter?actionId=10001', {
                    headers: {
                        'X-GREENLIGHT-TOKEN': token
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.waitStatus === 'READY') {
                        if (redirectUrl) {
                            window.location.href = redirectUrl;
                        } else {
                            // 기본 페이지로 이동 (예: 홈)
                            window.location.href = '/';
                        }
                    } else {
                        setTimeout(poll, 2000); // 2초마다 폴링
                    }
                })
                .catch(error => {
                    console.error('폴링 중 오류 발생:', error);
                    setTimeout(poll, 5000); // 오류 발생 시 5초 후 재시도
                });
            };

            window.onload = poll;
        })();
    </script>
</head>
<body>
    <h1>대기 중입니다. 잠시만 기다려주세요.</h1>
</body>
</html>
