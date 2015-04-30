<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Register a new store</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" enctype="multipart/form-data" action="${ pageContext.request.contextPath }/stores/register">
          <div class="form-field">
            <label class="field-label">Name *</label>
            <input class="field-control" type="text" name="displayName" value="${ field_displayName }" />

            <c:if test="${ not empty form_error and (form_error.fieldName == 'displayName' or form_error.fieldName == 'name' ) }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>

          </div>
          <div class="field-label">
            <label class="field-label">Website URL *</label>
            <input class="field-control" type="text" name="url" value="${ field_url }" />

            <c:if test="${ not empty form_error and form_error.fieldName == 'url' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>

          </div>
          <div class="form-field">
            <label class="field-label">Image</label>
            <div class="field-control-group">
              <input class="field-control" type="text" name="imageName" readonly />
              <span class="field-control-btn">
                <span class="btn btn-default btn-file">
                  <span class="fa fa-folder-open"></span>
                  <span class="text-plain">Browser</span>
                  <input class="field-control" type="file" name="imageData" accept=".png" />
                </span>
              </span>
            </div>
            <c:if test="${ not empty form_error and form_error.fieldName == 'imageBase64' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>
          </div>
          <div class="form-field">
            <label class="field-label">Comment</label>
            <textarea class="field-control" name="comment" rows="4">${ field_comment }</textarea>

            <c:if test="${ not empty form_error and form_error.fieldName == 'comment' }">
              <p class="field-error">${ form_error.fieldError }</p>
            </c:if>

          </div>

          <p>* Required fields</p>

          <div class="form-options">
            <button type="submit" class="btn btn-warning btn-sm-10 btn-md-5">Register</button>
          </div>
        </form>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->
