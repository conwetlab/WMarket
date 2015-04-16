<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/views/stores/header.jsp" />

<c:set var="isStoreOwner" value="${ store.creator.userName == user.userName }"></c:set>

<c:choose>
  <c:when test="${ isStoreOwner }">
    <div class="row container-fluid">
      <div class="col-sm-10 col-md-8 col-md-offset-1 col-lg-6 col-lg-offset-2">
        <div class="panel panel-default">
          <div class="panel-heading">
            <div class="panel-title">General Information</div>
          </div>
          <div class="panel-body row">
            <form class="col-sm-10 col-lg-8 col-lg-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/about">

              <div class="form-field">
                <label class="text-plain">Name *</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="form-control" type="text" name="displayName" value="${ field_displayName }" />
                    <c:if test="${ form_error.fieldName == 'displayName' }">
                      <div class="form-field-error">${ form_error.fieldError }</div>
                    </c:if>
                  </c:when>
                  <c:otherwise>
                    <input class="form-control" type="text" name="displayName" value="${ store.displayName }" />
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field">
                <label class="text-plain">Website URL *</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="form-control" type="text" name="url" value="${ field_url }" />
                    <c:if test="${ form_error.fieldName == 'url' }">
                      <div class="form-field-error">${ form_error.fieldError }</div>
                    </c:if>
                  </c:when>
                  <c:otherwise>
                    <input class="form-control" type="text" name="url" value="${ store.url }" />
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field">
                <label class="text-plain">Comment</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <textarea class="form-control" name="comment" rows="3">${ field_comment }</textarea>
                    <c:if test="${ form_error.fieldName == 'comment' }">
                      <div class="form-field-error">${ form_error.fieldError }</div>
                    </c:if>
                  </c:when>
                  <c:otherwise>
                    <textarea class="form-control" name="comment" rows="3">${ store.comment }</textarea>
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field readonly">
                <label class="text-plain">Registration date</label>
                <div class="form-control"><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></div>
              </div>

              <p class="text-plain text-default">* Required fields</p>
              <div class="form-options">
                <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Save changes</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div><!-- /.container-fluid -->

    <div class="row container-fluid">
      <div class="col-sm-10 col-md-8 col-md-offset-1 col-lg-6 col-lg-offset-2">
        <div class="panel panel-default">
          <div class="panel-heading">
            <span class="panel-title">Delete operation</span>
          </div>
          <div class="panel-body row">
            <form class="col-sm-10 col-lg-8 col-lg-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/delete">
              <p class="text-justify">This operation cannot be undone. All the offerings and descriptions that are contained in this store will be deleted too. Please be certain.</p>
              <div class="form-options">
                <button type="submit" class="btn btn-danger btn-sm-10 btn-md-5">Delete store</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div><!-- /.container-fluid -->

  </c:when>
  <c:otherwise>
    <div class="row container-fluid">
      <div class="col-sm-10 col-md-8 col-md-offset-1 col-lg-6 col-lg-offset-2">
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
      </div>
    </div><!-- /.container-fluid -->
  </c:otherwise>
</c:choose>
