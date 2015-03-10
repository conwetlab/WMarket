<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">
  <div class="col-smartphone-10 col-tablet-8 col-tablet-offset-1 col-desktop-6 col-desktop-offset-2">
    <div class="alert alert-${ statusCode < 500 ? 'warning' : 'danger' }">
      <div class="alert-heading"><strong>${ statusCode } - ${ statusPhrase }</strong></div>
      <div class="alert-content">${ content }</div>
    </div>
  </div>
</div>
