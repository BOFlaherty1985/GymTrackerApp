package co.uk.gymtracker.controllers;

import co.uk.gymtracker.dao.GymUserDao;
import co.uk.gymtracker.dao.GymUserDataDao;
import co.uk.gymtracker.logging.PerformanceLogging;
import co.uk.gymtracker.model.GymUser;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description Here
 *
 * @author Benjamin O'Flaherty
 * @date Created on: 30/05/14
 * @project GymTrackerApp
 */
public abstract class AbstractGymController {

    @Autowired
    public GymUserDao userDao;

    @Autowired
    public GymUserDataDao gymDataDao;

    @Autowired
    public PerformanceLogging performanceLogging;

    protected final XLogger logger = XLoggerFactory.getXLogger(AbstractGymController.class
            .getName());

    public abstract ModelAndView executeEntryPage(ModelAndView mav);

    protected GymUser getLoggedInUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();

        GymUser gymUser =  userDao.findGymUser(ctx.getAuthentication().getName());

        if(gymUser != null) {
            logger.info("gymUser found [" + gymUser.getUsername() + "]");
        }

        return gymUser;
    }

    protected Slf4JStopWatch createStopWatchInstance() {
        return new Slf4JStopWatch();
    }

    protected void runPerformanceLogging(String className, String methodName, StopWatch watch) {
        performanceLogging.isMethodProcessingBelowThreshold(className, methodName, watch);
    }

    @ModelAttribute("exercises")
    public List<String> listActivities() {

        List<String> activity = new ArrayList<String>();
        activity.add("Running");
        activity.add("Cycling");
        activity.add("Rowing");

        return activity;
    }

    @ModelAttribute("activityDuration")
    public List<String> listActivityDuration() {

        List<String> activityDuration = new ArrayList<String>();
        activityDuration.add("");
        activityDuration.add("15");
        activityDuration.add("30");
        activityDuration.add("45");
        activityDuration.add("60");

        return activityDuration;
    }

    @ModelAttribute("userRoles")
    public List<String> availableUserRoles() {
        List<String> userRoles = new ArrayList<String>();
        userRoles.add("ROLE_USER");
        userRoles.add("ROLE_ADMIN");
        userRoles.add("ROLE_DENIED");

        return userRoles;
    }
}
