<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="isStoreOwner" value="${ store.creator.userName == user.userName }"></c:set>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/stores/header.jsp" />
  </div>
  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">

    <c:choose>
      <c:when test="${ isStoreOwner }">

        <div class="panel panel-default">
          <div class="panel-heading">
            <div class="panel-title">General Information</div>
          </div>
          <div class="panel-body row">
            <form class="col-sm-10 col-lg-8 col-lg-offset-1" method="post" enctype="multipart/form-data" action="${ pageContext.request.contextPath }/stores/${ store.name }/about">

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
                    <input class="field-control" type="text" name="displayName" value="${ store.displayName }" />
                  </c:otherwise>
                </c:choose>
              </div><!-- /.form-field -->

              <div class="form-field">
                <label class="field-label">Website URL *</label>

                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="field-control" type="text" name="url" value="${ field_url }" />

                    <c:if test="${ form_error.fieldName == 'url' }">
                      <p class="field-error">${ form_error.fieldError }</p>
                    </c:if>

                  </c:when>
                  <c:otherwise>
                    <input class="field-control" type="text" name="url" value="${ store.url }" />
                  </c:otherwise>
                </c:choose>
              </div><!-- /.form-field -->

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
                </div><!-- /.field-control-group -->

                <c:if test="${ not empty form_error and form_error.fieldName == 'imageBase64' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>

              </div><!-- /.form-field -->

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
                    <textarea class="field-control" name="comment" rows="3">${ store.comment }</textarea>
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field">
                <label class="field-label">Registration date</label>
                <div class="field-control static"><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></div>
              </div>

              <p>* Required fields</p>

              <div class="form-options">
                <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Save changes</button>
              </div>
            </form>
          </div>
        </div>

      </c:when>
      <c:otherwise>

        <div class="panel panel-default">
          <div class="row panel-body">
            <dl class="dl-vertical col-sm-10 col-md-5 visible-sm-margin">
              <dt>Comment</dt>
              <c:choose>
                <c:when test="${ not empty store.comment }">
                  <dd>${ store.comment }</dd>
                </c:when>
                <c:otherwise>
                  <dd>No comment provided.</dd>
                </c:otherwise>
              </c:choose>
              <dt>Website URL</dt>
              <dd>${ store.url }</dd>
            </dl>
            <dl class="dl-vertical col-sm-10 col-md-5">
              <dt>Owner name</dt>
              <dd>${ store.creator.displayName }</dd>
              <dt>Registration date</dt>
              <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></dd>
            </dl>
          </div>
        </div>

      </c:otherwise>
    </c:choose>

  </div>
</div>
