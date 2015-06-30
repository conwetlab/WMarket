package org.fiware.apps.marketplace.controllers.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.CategoryBo;
import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


@Component
@Path("category")
public class CategoryController extends AbstractController {

	@Autowired private CategoryBo categoryBo;

	private static Logger logger = LoggerFactory.getLogger(CategoryController.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{categoryName}")
	public Response listView(
			@Context HttpServletRequest request,
			@PathParam("categoryName") String categoryName) {

		ModelAndView view;
		ModelMap model = new ModelMap();
		ResponseBuilder builder;

		try {
			model.addAttribute("user", getCurrentUser());

			Category category = categoryBo.findByName(categoryName);

			model.addAttribute("title", category.getDisplayName() +  " - Category - " + getContextName());
            model.addAttribute("category", category);
            model.addAttribute("viewName", "FilterByCategory");

			addFlashMessage(request, model);

			view = new ModelAndView("offering.list", model);
			builder = Response.ok();
		} catch (UserNotFoundException e) {
			logger.warn("User not found", e);

			view = buildErrorView(Status.INTERNAL_SERVER_ERROR, e.getMessage());
			builder = Response.serverError();
		} catch (CategoryNotFoundException e) {
			logger.info("Category not found", e);

			view = buildErrorView(Status.NOT_FOUND, e.getMessage());
			builder = Response.status(Status.NOT_FOUND);
		}

		return builder.entity(view).build();
	}
}
