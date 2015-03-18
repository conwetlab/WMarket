<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">
  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <div class="panel-title">Welcome to WMarket</div>
      </div>
      <div class="row panel-body">

        <c:if test="${ not empty param.err }">
          <div class="alert alert-danger">
            <span class="fa fa-times-circle"></span> The username and password do not match.
          </div>
        </c:if>

        <c:if test="${ not empty param.out }">
          <div class="alert alert-success">
            <span class="fa fa-check-circle"></span> You've logged out successfully.
          </div>
        </c:if>

        <form class="col-sm-8 col-sm-offset-1" name="login_form" method="post" action="<c:url value='j_spring_security_check' />">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <div class="form-field">
            <label class="text-plain">Username</label>
            <input class="form-control" type="text" name="username" />
          </div>
          <div class="form-field">
            <label class="text-plain">Password</label>
            <input class="form-control" type="password" name="password" />
          </div>
          <div class="form-options">
            <button class="btn btn-primary-lighter btn-sm-10 btn-md-5" type="submit">Sign In</button>
          </div>
        </form>

      </div>
    </div>
  </div>
</div>
