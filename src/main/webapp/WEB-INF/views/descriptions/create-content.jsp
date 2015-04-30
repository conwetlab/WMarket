<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">Upload a new description</span>
      </div>
      <div class="panel-body row">
        <c:choose>
          <c:when test="${ not empty storeList }">

            <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/descriptions/register">
              <div class="form-field">
                <label class="field-label">Store *</label>
                <select class="field-control" name="storeName">
                  <c:forEach var="store" items="${ storeList }">
                    <c:choose>
                      <c:when test="${ not empty field_storeName and field_storeName == store.name }">
                        <option value="${ store.name }" selected>${ store.displayName }</option>
                      </c:when>
                      <c:otherwise>
                        <option value="${ store.name }">${ store.displayName }</option>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>

                </select>
                <c:if test="${ not empty form_error and form_error.fieldName == 'storeName' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-field">
                <label class="field-label">Name *</label>
                <input class="field-control" type="text" name="displayName" value="${ field_displayName }" />

                <c:if test="${ not empty form_error and (form_error.fieldName == 'displayName' or form_error.fieldName == 'name') }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-field">
                <label class="field-label">URL to Linked USDL file * <a target="_blank" href="http://linked-usdl.org/"><span class="fa fa-info-circle"></span></a></label>
                <input class="field-control" type="text" name="url" value="${ field_url }" />

                <c:if test="${ not empty form_error and form_error.fieldName == 'url' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-field">
                <label class="field-label">Comment</label>
                <textarea class="field-control" name="comment" rows="4">${ field_comment }</textarea>

                <c:if test="${ not empty form_error and form_error.fieldName == 'comment' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <p>* Required fields</p>

              <div class="form-options">
                <button type="submit" class="btn btn-warning btn-sm-10 btn-md-5">Upload</button>
              </div>
            </form>

          </c:when>
          <c:otherwise>
            <div class="alert alert-info">
              <span class="fa fa-info-circle"></span> To upload descriptions, there must be at least one store. Go to <a class="alert-link" href="${ pageContext.request.contextPath }/stores/register">register a store</a>.
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

</div><!-- /.container-fluid -->
