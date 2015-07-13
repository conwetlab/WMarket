<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="isOwner" value="${ description.creator.userName == user.userName }"></c:set>

<div class="container">
  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/descriptions/header.jsp" />
  </div>
  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">
    <div class="panel panel-default">
      <div class="panel-heading text-center">
        <span class="panel-title">General information</span>
      </div>
      <div class="panel-body">
      <c:choose>
      <c:when test="${ isOwner }">

        <form  class="col-md-8 col-md-offset-1" name="description_update_form" method="post" action="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">
          <div class="form-field">
            <label class="field-label">Registered At</label>
            <div class="field-control static date"><fmt:formatDate pattern="MMM dd, yyyy HH:mm:ss" value="${ description.createdAt }" /></div>
          </div>
          <div class="form-field">
            <label class="field-label">Updated At</label>
            <div class="field-control static date"><fmt:formatDate pattern="MMM dd, yyyy HH:mm:ss" value="${ description.updatedAt }" /></div>
          </div>
          <div class="form-options">
            <button type="submit" class="btn btn-success">
              <span class="btn-text">Save changes</span>
            </button>
          </div>
        </form>

      </c:when>
      <c:otherwise>

        <div class="dl-group">
          <dl class="dl-block">
            <dt>Comment</dt>
            <c:choose>
              <c:when test="${ not empty description.comment }">
                <dd class="text-justify">${ description.comment }</dd>
              </c:when>
              <c:otherwise>
                <dd>No comment provided.</dd>
              </c:otherwise>
            </c:choose>
          </dl>
          <dl>
            <dt>Name</dt>
            <dd>${ description.displayName }</dd>
          </dl>
          <dl>
            <dt>Creator name</dt>
            <dd>${ description.creator.displayName }</dd>
          </dl>
          <dl>
            <dt>Registered At</dt>
            <dd class="date"><fmt:formatDate pattern="MMM dd, yyyy HH:mm:ss" value="${ description.createdAt }" /></dd>
          </dl>
          <dl>
            <dt>Updated At</dt>
            <dd class="date"><fmt:formatDate pattern="MMM dd, yyyy HH:mm:ss" value="${ description.updatedAt }" /></dd>
          </dl>
        </div>

      </c:otherwise>
      </c:choose>
      </div>
    </div>
    <div class="panel panel-default">
      <div class="panel-heading text-center">
        <span class="panel-title">Store information</span>
      </div>
      <div class="panel-body">
        <div class="dl-group">
          <dl>
            <dt>Name</dt>
            <dd><a href="${ pageContext.request.contextPath }/stores/${ store.name }/offerings">${ store.displayName }</a></dd>
          </dl>
          <dl>
            <dt>Creator name</dt>
            <dd>${ store.creator.displayName }</dd>
          </dl>
          <dl>
            <dt>Registered At</dt>
            <dd class="date"><fmt:formatDate pattern="MMM dd, yyyy" value="${ store.createdAt }" /></dd>
          </dl>
        </div>
      </div>
    </div>
  </div>
</div>