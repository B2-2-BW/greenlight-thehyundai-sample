<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>주문서</title>
    <meta charset="UTF-8">
</head>
<body>
<h2>주문서</h2>
<p>상품코드: ${slitmCd}</p>
<p>상품명: ${slitmNm}</p>
<p>상품설명: ${slitmDesc}</p>
<form action="/orderComplete" method="post">
    <button type="submit">결제하기</button>
</form>
</body>
</html>