package co.uk.gymtracker.controllers;

import co.uk.gymtracker.model.GymLogData;
import co.uk.gymtracker.model.GymUser;
import co.uk.gymtracker.model.form.GymSessionForm;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * GymDataInputController
 *
 * @author Benjamin O'Flaherty
 * @date Created on: 14/04/14
 * @project GymTrackerApp
 */
@Controller
public class GymDataInputController extends AbstractGymController {

    /**
     * Displays the addGymSessionForm to the user
     *
     * @return
     */
    @Override
    @RequestMapping(value="/addGymSessionForm")
    public ModelAndView executeEntryPage(ModelAndView mav) {

        logger.entry();

        mav.setViewName("addGymLog");
        mav.addObject(new GymSessionForm());

        logger.exit();

        return mav;
    }

    /**
     * POST User entered Gym Session Data to the server (incl. Validation)
     *
     * @param gymSessionForm
     * @param errors
     *
     * @return
     */
    @RequestMapping(value="/addGymSession", method = RequestMethod.POST)
    public ModelAndView addGymSessionData(@Valid GymSessionForm gymSessionForm, Errors errors) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        logger.entry(gymSessionForm, errors);

        StopWatch watch = new Slf4JStopWatch();

        ModelAndView mav = new ModelAndView();

        // TODO Add a Custom Validator to validate the different potential data sets entered, cardio or weight

        if(errors.hasErrors()) {
            mav.setViewName("addGymLog");
            return mav;
        } else {
            // extract the GymUser from the security context
            GymUser user = getLoggedInUser();

            GymLogData gymSessionData = new GymLogData(
                    gymSessionForm.getDate(), gymSessionForm.getDuration(), gymSessionForm.getCardioExercise(),
                    gymSessionForm.getActivityDuration(), gymSessionForm.getDistance(), gymSessionForm.getLevel(),
                    gymSessionForm.getWeight(), gymSessionForm.getReps(), gymSessionForm.getCalories(), gymSessionForm.getUserWeight(),
                    "exercise"
            );



            logger.info("added gymSessionData [" + gymSessionData + "]");

            // build and update the list of GymSessions for a user.
            List<GymLogData> gymSessions = new ArrayList<GymLogData>();

            if(user.getUserSessions() != null) {
                gymSessions = user.getUserSessions();
            }

            gymSessions.add(gymSessionData);

            user.setUserSessions(gymSessions);

            // update the GymUser document
            userDao.updateGymUser(user);

            mav.addObject(gymDataDao.findAllUserGymData());
        }

        // log method performance
        runPerformanceLogging(this.getClass().getName(), methodName, watch);

        logger.exit();

        return new ModelAndView("redirect:/userLog/show");
    }

}