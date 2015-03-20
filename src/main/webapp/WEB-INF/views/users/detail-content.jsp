<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default-darker">
      <div class="panel-heading">
        <span class="fa-avatar fa-stack">
          <i class="fa fa-circle fa-stack-2x"></i>
          <i class="fa fa-user fa-stack-1x fa-inverse"></i>
        </span>
        <span class="panel-title">Personal Information</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/account">

          <div class="form-field readonly">
            <label class="text-plain">Username</label>
            <input class="form-control" type="text" name="userName" value="${ user.userName }" />
          </div>

          <div class="form-field">
            <label class="text-plain">Full name *</label>
            <c:choose>
              <c:when test="${ not empty form_error and form_error.fieldName == 'displayName' }">
                <input class="form-control" type="text" name="displayName" value="${ field_displayName }" />
                <div class="form-field-error">${ form_error.fieldError }</div>
              </c:when>
              <c:otherwise>
                <input class="form-control" type="text" name="displayName" value="${ user.displayName }" />
              </c:otherwise>
            </c:choose>
          </div>

          <div class="form-field">
            <label class="text-plain">Email *</label>
            <c:choose>
              <c:when test="${ not empty form_error and form_error.fieldName == 'email' }">
                <input class="form-control" type="text" name="email" value="${ field_email }" />
                <div class="form-field-error">${ form_error.fieldError }</div>
              </c:when>
              <c:otherwise>
                <input class="form-control" type="text" name="email" value="${ user.email }" />
              </c:otherwise>
            </c:choose>
          </div>

          <div class="form-field">
            <label class="text-plain">Company</label>
            <c:choose>
              <c:when test="${ not empty form_error and form_error.fieldName == 'company' }">
                <input class="form-control" type="text" name="company" value="${ field_company }" />
                <div class="form-field-error">${ form_error.fieldError }</div>
              </c:when>
              <c:otherwise>
                <input class="form-control" type="text" name="company" value="${ user.company }" />
              </c:otherwise>
            </c:choose>
          </div>

          <p class="text-plain text-default">* Required fields</p>
          <div class="form-options">
            <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Update Profile</button>
          </div>
        </form>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Change Password</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/account/password">
        
          <div class="form-field">
            <label class="text-plain">Old password</label>
            <input class="form-control" type="password" name="oldPassword" />
            <c:if test="${ not empty form_error and form_error.fieldName == 'oldPassword' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-field">
            <label class="text-plain">New password</label>
            <input class="form-control" type="password" name="password" />
            <c:if test="${ not empty form_error and form_error.fieldName == 'password' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-field">
            <label class="text-plain">Confirm new password</label>
            <input class="form-control" type="password" name="passwordConfirm" />
            <c:if test="${ not empty form_error and form_error.fieldName == 'passwordConfirm' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>
          </div>

          <div class="form-options">
            <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Update Password</button>
          </div>
        </form>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Delete Account</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/account/delete">
          <p>This operation cannot be undone. All your stores, descriptions and offerings will be deleted. Please, be careful.</p>
          <div class="form-options">
            <button type="submit" class="btn btn-danger btn-sm-10 btn-md-5">Delete this account</button>
          </div>
        </form>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->
