<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row container-fluid">
  <c:choose>

    <c:when test="${ not empty descriptions }">
      <div class="col-sm-10 col-md-8 col-md-offset-1 col-lg-6 col-lg-offset-2">

        <c:forEach var="description" items="${ descriptions }">
          <div class="panel panel-default">
            <div class="panel-body">

              <div class="dl-group dl-responsive">
                <dl>
                  <dt>Name</dt>
                  <dd><a href="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">${ description.displayName }</a></dd>
                </dl>

                <dl>
                  <dt>URL to Linked USDL file</dt>
                  <dd>${ description.url }</dd>
                </dl>

                <dl>
                  <dt>Upload date</dt>
                  <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ description.registrationDate }" /></dd>
                </dl>
              </div>

              <p class="text-bold">Offerings</p>

              <div class="well">
                <c:forEach var="offering" items="${ description.offerings }">

                  <a class="offering-item" href="${ pageContext.request.contextPath }/offerings/${ description.store.name }/${ description.name }/${ offering.name }">
                    <span class="offering-heading">
                      <span class="image-thumbnail image-thumbnail-sm">
                        <img class="image image-rounded" src="${ offering.imageUrl }" />
                      </span>
                    </span>
                    <span class="offering-body">
                      <span class="offering-name">${ offering.displayName }</span>
                      <span class="offering-version">${ offering.version }</span>
                    </span>
                  </a>

                </c:forEach>
              </div>

            </div>
          </div>
        </c:forEach>

      </div>

    </c:when>

    <c:otherwise>
      <div class="alert alert-warning col-sm-10">
        <span class="fa fa-exclamation-circle"></span> No description available.
      </div>
    </c:otherwise>

  </c:choose>
</div>
