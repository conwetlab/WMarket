<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Register a new store</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/register">
          <div class="form-field">
            <label class="text-plain">Name *</label>
            <input class="form-control" type="text" name="displayName" value="${ field_displayName }" />

            <c:if test="${ not empty form_error and (form_error.fieldName == 'displayName' or form_error.fieldName == 'name' ) }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>

          </div>
          <div class="form-field">
            <label class="text-plain">URL *</label>
            <input class="form-control" type="text" name="url" value="${ field_url }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'url' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>

          </div>
          <div class="form-field">
            <label class="text-plain">Description</label>
            <textarea class="form-control" name="description" rows="4">${ field_description }</textarea>

            <c:if test="${ not empty form_error and form_error.fieldName == 'description' }">
              <div class="form-field-error">${ form_error.fieldError }</div>
            </c:if>

          </div>
          <p class="text-plain text-default">* Required fields</p>
          <div class="form-options">
            <button type="submit" class="btn btn-warning btn-sm-10 btn-md-5">Register</button>
          </div>
        </form>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->
