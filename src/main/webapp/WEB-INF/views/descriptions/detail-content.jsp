<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row container-fluid">
  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">General information</span>
      </div>
      <div class="panel-body row">
        <form  class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">
          <div class="form-field">
            <label class="text-plain">Name *</label>
            <input class="form-control" type="text" name="displayName" value="${ description.displayName }" />
          </div>

          <div class="form-field">
            <label class="text-plain">URL to Linked USDL file *</label>
            <input class="form-control" type="text" name="url" value="${ description.url }" />
          </div>

          <div class="form-field">
            <label class="text-plain">Comment</label>
            <input class="form-control" type="text" name="comment" value="${ description.comment }" />
          </div>

          <div class="form-field readonly">
            <label class="text-plain">Upload Date</label>
            <div class="form-control"><fmt:formatDate pattern="yyyy-MM-dd" value="${ description.registrationDate }" /></div>
          </div>

          <p class="text-plain text-default">* Required fields</p>
          <div class="form-options">
            <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Update</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->

<div class="row container-fluid">
  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">List of offerings</span>
      </div>
      <div class="panel-body row">
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
    </div>
  </div>
</div><!-- /.container-fluid -->

<div class="row container-fluid">
  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Delete description</span>
      </div>
      <div class="panel-body row">
        <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }/delete">
          <p class="text-justify">This operation cannot be undone. All the offerings are contained in this description will be deleted too. Please be certain.</p>
          <div class="form-options">
            <button type="submit" class="btn btn-danger btn-sm-10 btn-md-5">Delete</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div><!-- /.container-fluid -->
