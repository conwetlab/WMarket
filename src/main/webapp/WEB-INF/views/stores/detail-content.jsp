<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/views/stores/header.jsp" />

<c:set var="isStoreOwner" value="${ store.creator.userName == user.userName }"></c:set>

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
          <dt>Owner Name</dt>
          <c:choose>
            <c:when test="${ isStoreOwner }">
              <dd>You're the owner</dd>
            </c:when>
            <c:otherwise>
              <dd>${ store.creator.displayName }</dd>
            </c:otherwise>
          </c:choose>
          <dt>Registration Date</dt>
          <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></dd>
        </dl>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->
