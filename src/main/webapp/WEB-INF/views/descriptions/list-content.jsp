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
          <div class="tab active"><a href="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions">Descriptions</a></div>
          <div class="tab"><a href="${ pageContext.request.contextPath }/stores/${ store.name }/details">About</a></div>
        </div>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->

<c:choose>
  <c:when test="${ not empty store.descriptions }">
    <c:forEach var="offeringServ" items="${ store.descriptions }">
      <div class="row container-fluid">
        <div class="col-sm-10">
          <div
          <c:choose>
            <c:when test="${ not empty current_description and current_description == offeringServ.name }">
              class="panel panel-default"
            </c:when>
            <c:otherwise>
              class="panel panel-default collapsed"
            </c:otherwise>
          </c:choose>
            >
            <div class="panel-heading with-options">
              <span class="panel-title">${ offeringServ.displayName }</span>
              <span class="panel-options">
                <span class="opt opt-collapse">
                  <span class="fa fa-caret-down"></span>
                </span>
                <span class="opt opt-danger opt-remove" data-resource="form_${ offeringServ.name }">
                  <span class="fa fa-trash"></span>
                </span>
              </span>
            </div>
            <div class="row panel-body">

              <form id="form_update_${ offeringServ.name }" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions/${ offeringServ.name }">
                <div class="row">
                  <div class="form-field col-sm-10 col-md-5">
                    <label class="text-plain">Name *</label>
                    <input class="form-control" type="text" name="displayName" value="${ offeringServ.displayName }" />
                  </div>

                  <div class="form-field col-sm-10 col-md-5">
                    <label class="text-plain">Description</label>
                    <input class="form-control" type="text" name="description" value="${ offeringServ.description }" />
                  </div>
                </div>

                <div class="row">
                  <div class="form-field col-sm-10 col-md-5">
                    <label class="text-plain">URL *</label>
                    <input class="form-control" type="text" name="url" value="${ offeringServ.url }" />
                  </div>

                  <div class="form-field readonly col-sm-10 col-md-5">
                    <label class="text-plain">Number of offerings</label>
                    <div class="form-control">${ offeringServ.offerings.size() }</div>
                  </div>
                </div>

                <div class="row">
                  <div class="form-field readonly col-sm-10 col-md-5">
                    <label class="text-plain">Committer Name</label>
                    <div class="form-control">${ offeringServ.creator.displayName }</div>
                  </div>

                  <div class="form-field readonly col-sm-10 col-md-5">
                    <label class="text-plain">Upload Date</label>
                    <div class="form-control"><fmt:formatDate pattern="yyyy-MM-dd" value="${ offeringServ.registrationDate }" /></div>
                  </div>
                </div>

                <p class="text-plain text-default">* Required fields</p>
                <div class="form-options">
                  <button type="submit" class="btn btn-success btn-sm-10 btn-md-3 btn-lg-2">Save changes</button>
                </div>
              </form>
              <form name="form_${ offeringServ.name }" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions/${ offeringServ.name }/delete"></form>
            </div>
          </div>
        </div>
      </div><!-- /.container-fluid -->
    </c:forEach>

  </c:when>
  <c:otherwise>
    <div class="row container-fluid">
      <div class="col-sm-10">
        <div class="alert alert-warning col-sm-10">
          <span class="fa fa-exclamation-circle"></span> No offering service available.
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>
