<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-4 col-lg-3">

    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="image-thumbnail image-thumbnail-lg">
          <img class="image image-rounded" src="${ offering.imageUrl }">
        </span>
        <div class="panel-title">${ offering.displayName }</div>
      </div>
      <div class="panel-body">
        <div class="tab-group tab-group-vertical">

          <div class="tab active">
            <a href="${ pageContext.request.contextPath }/offerings/${ offering.describedIn.store.name }/${ offering.describedIn.name }/${ offering.name }">
              <span class="fa fa-newspaper-o"></span>
              <span class="hidden-sm">General</span>
            </a>
          </div><!-- /.tab -->

          <c:choose>
            <c:when test="${ not empty bookmark }">

              <div class="btn-toggle-bookmark tab tab-danger">
                <a href="javascript:WMarket.view.toggleBookmark();">
                  <span class="fa fa-bookmark"></span>
                  <span class="hidden-sm">Remove bookmark</span>
                </a>
              </div><!-- /.tab -->

            </c:when>
            <c:otherwise>

              <div class="btn-toggle-bookmark tab">
                <a href="javascript:WMarket.view.toggleBookmark();">
                  <span class="fa fa-bookmark"></span>
                  <span class="hidden-sm">Add bookmark</span>
                </a>
              </div><!-- /.tab -->

            </c:otherwise>
          </c:choose>

        </div>
      </div>
    </div><!-- /.panel -->

  </div>
  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">

    <div class="panel panel-default">
      <div class="panel-heading">
        <div class="panel-title">General information</div>
      </div><!-- /.panel-heading -->

      <div class="panel-body">
        <div class="dl-group dl-vertical">

          <dl class="dl-block">
            <dt>Description</dt>
            <dd class="text-justify">${ offering.description }</dd>
          </dl>

          <dl>
            <dt>Version</dt>
            <dd>${ offering.version }</dd>
          </dl>

          <dl>
            <dt>Developer name</dt>
            <dd>${ offering.describedIn.creator.displayName }</dd>
          </dl>

          <dl>
            <dt>Upload date</dt>
            <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ offering.describedIn.registrationDate }" /></dd>
          </dl>

        </div>
      </div><!-- /.panel-body -->
    </div><!-- /.panel -->

    <div class="panel panel-default">
      <div class="panel-heading">
        <c:if test="${ not empty offering.describedIn.store.imagePath }">
          <span class="image-thumbnail">
            <img class="image image-circle" src="${ pageContext.request.contextPath }/${ offering.describedIn.store.imagePath }">
          </span>
        </c:if>

        <span class="panel-title">Store information</span>
      </div>
      <div class="panel-body">
        <div class="dl-group dl-vertical">
          <dl>
            <dt>Name</dt>
            <dd><a href="${ pageContext.request.contextPath }/stores/${ offering.describedIn.store.name }/offerings">${ offering.describedIn.store.displayName }</a></dd>
          </dl>
          <dl>
            <dt>Creator name</dt>
            <dd>${ offering.describedIn.store.creator.displayName }</dd>
          </dl>
          <dl>
            <dt>Registration date</dt>
            <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ offering.describedIn.store.registrationDate }" /></dd>
          </dl>

        </div>
      </div>
    </div>

  </div><!-- /.col-* -->
</div><!-- /.row -->
