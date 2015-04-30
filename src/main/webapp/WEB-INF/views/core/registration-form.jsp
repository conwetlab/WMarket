<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">
  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <div class="panel-title">Sign up for WMarket</div>
      </div>
      <div class="row panel-body">

        <form class="col-sm-8 col-sm-offset-1" name="registration_form" method="post" action="${ pageContext.request.contextPath }/register">
          <div class="form-field">
            <label class="field-label">Full name</label>
            <input class="field-control" type="text" name="displayName" value="${ field_displayName }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'displayName' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>
          </div>

          <div class="form-field">
            <label class="field-label">E-mail</label>
            <input class="field-control" type="text" name="email" value="${ field_email }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'email' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>
          </div>

          <div class="form-field">
            <label class="field-label">Password</label>
            <input class="field-control" type="password" name="password" value="${ field_password }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'password' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>
          </div>

          <div class="form-field">
            <label class="field-label">Confirm your password</label>
            <input class="field-control" type="password" name="passwordConfirm"  value="${ field_passwordConfirm }"/>

            <c:if test="${ not empty form_error and form_error.fieldName == 'passwordConfirm' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>
          </div>

          <div class="form-options">
            <button class="btn btn-warning btn-sm-10 btn-md-5" type="submit">Create Account</button>
          </div>
        </form>

      </div>
    </div>
  </div>
</div>
