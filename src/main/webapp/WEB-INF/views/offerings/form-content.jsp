<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="row container-fluid">

	<div class="col-sm-10 col-md-6 col-md-offset-2 col-lg-4 col-lg-offset-3">
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <span class="panel-title">Upload a new offering</span>
	    </div>
	    <div class="panel-body row">
	      <form class="col-sm-10 col-md-8 col-md-offset-1 col-lg-8 col-lg-offset-1" method="POST" action="${ pageContext.request.contextPath }/stores/register/">
            <div class="form-field">
              <label class="text-plain">Store Name</label>
              <input class="form-control" type="text" name="displayName" placeholder="Store name" />
            </div>
            <div class="form-field">
              <label class="text-plain">Store URL</label>
              <input class="form-control" type="text" name="url" placeholder="Store URL" />
            </div>
            <div class="form-options">
              <button type="submit" class="btn btn-warning">Register</button>
            </div>
          </form>
        </div>
	  </div>
    </div>

</div><!-- /.container-fluid -->
