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
            <label class="text-plain">Full name</label>
            <input class="form-control" type="text" name="displayName" value="${ field_displayName }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'displayName' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-field">
            <label class="text-plain">E-mail</label>
            <input class="form-control" type="text" name="email" value="${ field_email }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'email' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-field">
            <label class="text-plain">Password</label>
            <input class="form-control" type="password" name="password" value="${ field_password }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'password' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-field">
            <label class="text-plain">Confirm your password</label>
            <input class="form-control" type="password" name="passwordConfirm"  value="${ field_passwordConfirm }"/>

            <c:if test="${ not empty form_error and form_error.fieldName == 'passwordConfirm' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <p class="text-plain text-default">All fields are required</p>
          <div class="form-options">
            <button class="btn btn-warning btn-sm-10 btn-md-5" type="submit">Create Account</button>
          </div>
        </form>

      </div>
    </div>
  </div>
</div>
