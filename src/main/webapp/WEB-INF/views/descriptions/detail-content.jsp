<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/descriptions/header.jsp" />
  </div>
  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">

    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">General information</span>
      </div>
      <div class="panel-body row">
        <form  class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">
          <div class="form-field">
            <label class="field-label">Name *</label>
            <c:choose>
              <c:when test="${ not empty form_error }">
                <input class="field-control" type="text" name="displayName" value="${ field_displayName }" />

                <c:if test="${ form_error.fieldName == 'displayName' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>

              </c:when>
              <c:otherwise>
                <input class="field-control" type="text" name="displayName" value="${ description.displayName }" />
              </c:otherwise>
            </c:choose>
          </div>

          <div class="form-field">
            <label class="field-label">URL to Linked USDL file *</label>

            <c:choose>
              <c:when test="${ not empty form_error }">
                <input class="field-control" type="text" name="url" value="${ field_url }" />

                <c:if test="${ form_error.fieldName == 'url' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>

              </c:when>
              <c:otherwise>
                <input class="field-control" type="text" name="url" value="${ description.url }" />
              </c:otherwise>
            </c:choose>

          </div>

          <div class="form-field">
            <label class="field-label">Comment</label>

            <c:choose>
              <c:when test="${ not empty form_error }">
                <textarea class="field-control" name="comment" rows="3">${ field_comment }</textarea>

                <c:if test="${ form_error.fieldName == 'comment' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>

              </c:when>
              <c:otherwise>
                <textarea class="field-control" name="comment" rows="3">${ description.comment }</textarea>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="form-field">
            <label class="field-label">Upload Date</label>
            <div class="field-control static"><fmt:formatDate pattern="yyyy-MM-dd" value="${ description.registrationDate }" /></div>
          </div>

          <p>* Required fields</p>

          <div class="form-options">
            <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Update</button>
          </div>
        </form>
      </div>
    </div>

    <div class="panel panel-default">
      <div class="panel-heading">

        <c:if test="${ not empty store.imagePath }">
          <span class="image-thumbnail">
            <img class="image image-circle" src="${ pageContext.request.contextPath }/${ store.imagePath }">
          </span>
        </c:if>

        <span class="panel-title">Store information</span>
      </div>
      <div class="panel-body">
        <div class="dl-group dl-vertical">
          <dl>
            <dt>Name</dt>
            <dd><a href="${ pageContext.request.contextPath }/stores/${ store.name }/offerings">${ store.displayName }</a></dd>
          </dl>
          <dl>
            <dt>Creator name</dt>
            <dd>${ store.creator.displayName }</dd>
          </dl>
          <dl>
            <dt>Registration date</dt>
            <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></dd>
          </dl>

        </div>
      </div>
    </div>

  </div>
</div>
