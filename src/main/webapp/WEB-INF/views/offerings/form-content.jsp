<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Upload new offerings</span>
      </div>
      <div class="panel-body row">
        <c:choose>
          <c:when test="${ not empty storeList }">

            <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/offerings/register">
              <div class="form-field">
                <label class="text-plain">Store Name</label>
                <select class="form-control" name="storeName">
                  <c:forEach var="store" items="${ storeList }">
                    <option value="${ store.name }">${ store.displayName }</option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-field">
                <label class="text-plain">Description Name</label>
                <input class="form-control" type="text" name="descriptionName" value="${ field_descriptionName }" />
              </div>

              <div class="form-field">
                <label class="text-plain">Description URL</label>
                <input class="form-control" type="text" name="descriptionURL" value="${ field_descriptionURL }" />
              </div>

              <p class="text-plain text-default">All fields are required</p>
              <div class="form-options">
                <button type="submit" class="btn btn-warning btn-sm-10 btn-md-5">Upload</button>
              </div>
            </form>

          </c:when>
          <c:otherwise>
            <div class="alert alert-info">
              <span class="fa fa-info-circle"></span> To upload offerings, you need to register at least one store. Go to <a class="alert-link" href="${ pageContext.request.contextPath }/stores/register">register store</a>.
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->
