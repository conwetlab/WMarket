<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/views/stores/header.jsp" />

<c:choose>
  <c:when test="${ not empty descriptions }">
    <c:forEach var="description" items="${ descriptions }">
      <div class="row container-fluid">
        <div class="col-sm-10 col-md-8 col-md-offset-1 col-lg-6 col-lg-offset-2">
          <div class="panel panel-default">
            <div class="panel-heading">
              <a class="panel-title" href="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">${ description.displayName }</a>
            </div>
            <div class="panel-body">
              <div class="row">
                <div class="col-sm-10">
                  <div class="well">
                    <c:forEach var="offering" items="${ description.offerings }">
                      <div class="panel panel-default offering-in-description">
                        <div class="panel-heading">
                          <div class="thumbnail thumbnail-xs">
                            <img src="${ offering.imageUrl }">
                          </div>
                          <a class="panel-title" href="${ pageContext.request.contextPath }/offerings/${ description.store.name }/${ description.name }/${ offering.name }">${ offering.displayName }</a>
                        </div>
                        <div class="panel-body">
                          <div class="offering-description">${ offering.description }</div>
                        </div>
                      </div>
                    </c:forEach>
                  </div>
                </div>
              </div>
              <div class="row">
                <dl class="dl-vertical col-sm-10 col-md-5 visible-sm-margin">
                  <dt>Comment</dt>
                  <c:choose>
                    <c:when test="${ not empty description.comment }">
                      <dd>${ description.comment }</dd>
                    </c:when>
                    <c:otherwise>
                      <dd>No comment provided.</dd>
                    </c:otherwise>
                  </c:choose>
                  <dt>URL to Linked USDL file</dt>
                  <dd>${ description.url }</dd>
                </dl>
                <dl class="dl-vertical col-sm-10 col-md-5">
                  <dt>Upload date</dt>
                  <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ description.registrationDate }" /></dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>

  </c:when>
  <c:otherwise>
    <div class="row container-fluid">
      <div class="alert alert-warning col-sm-10">
        <span class="fa fa-exclamation-circle"></span> No description available.
      </div>
    </div>
  </c:otherwise>
</c:choose>