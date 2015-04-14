<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>


<div class="row container-fluid">
  <div class="col-sm-10 col-md-10 col-lg-10">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title"><strong>${ store.displayName }</strong></span>
      </div>
      <div class="panel-body">
        <div class="nav-tabs">

          <div
          <c:choose>
            <c:when test="${ currentStoreView == 'offeringList' }">
              class="tab active"
            </c:when>
            <c:otherwise>
              class="tab"
            </c:otherwise>
          </c:choose>
            >
            <a href="${ pageContext.request.contextPath }/stores/${ store.name }/offerings">
              <span class="fa fa-cubes"></span>
              <span class="text-plain hidden-smartphone">All offerings</span>
            </a>
          </div><!-- /.tab -->

          <div
          <c:choose>
            <c:when test="${ currentStoreView == 'descriptionList' }">
              class="tab active"
            </c:when>
            <c:otherwise>
              class="tab"
            </c:otherwise>
          </c:choose>
            >
            <a href="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions">
              <span class="fa fa-archive"></span>
              <span class="text-plain hidden-smartphone">My descriptions</span>
            </a>
          </div><!-- /.tab -->

          <div
          <c:choose>
            <c:when test="${ currentStoreView == 'detail' }">
              class="tab active"
            </c:when>
            <c:otherwise>
              class="tab"
            </c:otherwise>
          </c:choose>
            >
            <a href="${ pageContext.request.contextPath }/stores/${ store.name }/about">
              <span class="fa fa-newspaper-o"></span>
              <span class="text-plain hidden-smartphone">About</span>
            </a>
          </div><!-- /.tab -->

        </div>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->
