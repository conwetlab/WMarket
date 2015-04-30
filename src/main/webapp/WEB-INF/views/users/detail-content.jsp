<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row container-fluid">

  <div class="col-sm-10 col-md-4 col-lg-3">

      <div class="panel panel-default-darker">
          <div class="panel-heading">
            <span class="image-thumbnail image-thumbnail-lg">
              <span class="image image-circle image-primary-ligther">
                <span class="fa fa-user fa-inverse"></span>
              </span>
            </span>

          <span class="panel-title">${ user.displayName }</span>
        </div><!-- /.panel-heading -->

        <div class="panel-body">
          <div class="tab-group tab-group-vertical">

            <div class="tab active">
              <a href="${ pageContext.request.contextPath }/account">
                <span class="fa fa-newspaper-o"></span>
                <span class="hidden-sm">Personal</span>
              </a>
            </div><!-- /.tab -->

            <c:if test="${ user.oauth2 eq false }">

              <!-- <div class="tab">
                <a href="#">
                  <span class="fa fa-key"></span>
                  <span class="hidden-sm">Credentials</span>
                </a>
              </div> -->

              <div class="tab tab-danger">
                <a href="javascript:deleteAccount()">
                  <span class="fa fa-trash"></span>
                  <span class="hidden-sm">Delete account</span>
                </a>
              </div><!-- /.tab -->

            </c:if>

        </div>
      </div>
    </div>

  </div>

  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">

    <c:choose>
      <c:when test="${ user.oauth2 eq true }">

        <div class="panel panel-default">
          <div class="panel-heading">
            <span class="panel-title">Personal Information</span>
          </div>
          <div class="panel-body">
            <div class="form-field">
              <label class="field-label">Username</label>
              <div class="field-control static">${ user.userName }</div>
            </div>

            <div class="form-field">
              <label class="field-label">Full name</label>
              <div class="field-control static">${ user.displayName }</div>
            </div>

            <div class="form-field">
              <label class="field-label">Email</label>
              <div class="field-control static">${ user.email }</div>
            </div>

            <div class="form-field">
              <label class="field-label">OAuth2</label>
              <input class="field-control" type="text" name="oauth2" value="Logged in through 'Identity Manager'" />
            </div>
          </div>
        </div>
      </c:when>
      <c:otherwise>

        <div class="panel panel-default">
          <div class="panel-heading">
            <span class="panel-title">Personal Information</span>
          </div>
          <div class="panel-body row">

          <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/account">

              <div class="form-field">
                <label class="field-label">Username</label>
                <input class="field-control static" type="text" name="userName" value="${ user.userName }" />
              </div>

              <div class="form-field">
                <label class="field-label">Full name *</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="field-control" type="text" name="displayName" value="${ field_displayName }" />

                    <c:if test="${ form_error.fieldName == 'displayName' }">
                      <p class="field-error">${ form_error.fieldError }</p>
                    </c:if>

                  </c:when>
                  <c:otherwise>
                    <input class="field-control" type="text" name="displayName" value="${ user.displayName }" />
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field">
                <label class="field-label">Email *</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="field-control" type="text" name="email" value="${ field_email }" />

                    <c:if test="${ form_error.fieldName == 'email' }">
                      <p class="field-error">${ form_error.fieldError }</p>
                    </c:if>

                  </c:when>
                  <c:otherwise>
                    <input class="field-control" type="text" name="email" value="${ user.email }" />
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="form-field">
                <label class="field-label">Company</label>
                <c:choose>
                  <c:when test="${ not empty form_error }">
                    <input class="field-control" type="text" name="company" value="${ field_company }" />

                    <c:if test="${ form_error.fieldName == 'company' }">
                      <p class="field-error">${ form_error.fieldError }</p>
                    </c:if>

                  </c:when>
                  <c:otherwise>
                    <input class="field-control" type="text" name="company" value="${ user.company }" />
                  </c:otherwise>
                </c:choose>
              </div>

              <p>* Required fields</p>

              <div class="form-options">
                <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Save changes</button>
              </div>
            </form>

          </div>
        </div>

        <div class="panel panel-default">
          <div class="panel-heading">
            <span class="panel-title">Change Password</span>
          </div>
          <div class="panel-body row">
            <form class="col-sm-8 col-sm-offset-1" method="post" action="${ pageContext.request.contextPath }/account/password">

              <div class="form-field">
                <label class="field-label">Old password</label>
                <input class="field-control" type="password" name="oldPassword" />
                <c:if test="${ not empty form_error and form_error.fieldName == 'oldPassword' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-field">
                <label class="field-label">New password</label>
                <input class="field-control" type="password" name="password" />
                <c:if test="${ not empty form_error and form_error.fieldName == 'password' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-field">
                <label class="field-label">Confirm new password</label>
                <input class="field-control" type="password" name="passwordConfirm" />
                <c:if test="${ not empty form_error and form_error.fieldName == 'passwordConfirm' }">
                  <p class="field-error">${ form_error.fieldError }</p>
                </c:if>
              </div>

              <div class="form-options">
                <button type="submit" class="btn btn-success btn-sm-10 btn-md-5">Update Password</button>
              </div>
            </form>
          </div>
        </div>

        <form name="account_delete_form" method="post" action="${ pageContext.request.contextPath }/account/delete">
          <!-- <p class="text-justify">This operation cannot be undone. All your stores, descriptions and offerings will be deleted too. Please be certain.</p> -->
        </form>

        <script>
          var deleteAccount = function deleteAccount() {
              return document.account_delete_form.submit();
          };
        </script>
      </c:otherwise>
    </c:choose>

  </div>
</div>
