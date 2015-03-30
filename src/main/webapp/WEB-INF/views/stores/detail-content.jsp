<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row container-fluid">
  <div class="col-sm-10 col-md-10 col-lg-10">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Welcome to <strong>${ store.displayName }</strong></span>
      </div>
      <div class="panel-body">
        <div class="nav-tabs">
          <div class="tab"><a href="${ pageContext.request.contextPath }/stores/${ store.name }/offerings">Offerings</a></div>
          <div class="tab"><a href="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions">Descriptions</a></div>
          <div class="tab active"><a href="${ pageContext.request.contextPath }/stores/${ store.name }/details">About</a></div>
        </div>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->

<div class="row container-fluid">
  <div class="col-sm-10 col-md-10 col-lg-10">
    <div class="panel panel-default">
      <div class="row panel-body">
        <dl class="dl-vertical col-sm-10 col-md-5 visible-sm-margin">
          <dt>Description</dt>
          <c:choose>
            <c:when test="${ not empty store.description }">
              <dd>${ store.description }</dd>
            </c:when>
            <c:otherwise>
              <dd>No description provided.</dd>
            </c:otherwise>
          </c:choose>
          <dt>URL</dt>
          <dd>${ store.url }</dd>
        </dl>
        <dl class="dl-vertical col-sm-10 col-md-5">
          <dt>Creator Name</dt>
          <dd>${ store.creator.displayName }</dd>
          <dt>Registration Date</dt>
          <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></dd>
        </dl>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->
