package com.scsse.workflow.service.impl;

import com.scsse.workflow.entity.Activity;
import com.scsse.workflow.entity.Recruit;
import com.scsse.workflow.entity.Tag;
import com.scsse.workflow.repository.ActivityRepository;
import com.scsse.workflow.repository.RecruitRepository;
import com.scsse.workflow.repository.TagRepository;
import com.scsse.workflow.service.ActivityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Alfred Fu
 * Created on 2019-02-19 20:18
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final ModelMapper modelMapper;

    private final ActivityRepository activityRepository;

    private final RecruitRepository recruitRepository;

    private final TagRepository tagRepository;

    @Autowired
    public ActivityServiceImpl(ModelMapper modelMapper, ActivityRepository activityRepository, RecruitRepository recruitRepository, TagRepository tagRepository) {
        this.modelMapper = modelMapper;
        this.activityRepository = activityRepository;
        this.recruitRepository = recruitRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Activity> findAllActivity() {
        return activityRepository.findAll();
    }

    @Override
    public List<Activity> findAllExpiredActivity() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().stream()
                // if the time now is greater than the signUpDeadline
                .filter(activity -> LocalDate.now().compareTo
                        (activity.getActivitySignUpDeadline().toInstant().atZone(ZoneId.of("Asia/Shanghai")).
                                toLocalDate()) > 0)
                // but less than the activity time
                .filter(activity -> LocalDate.now().compareTo
                        (activity.getActivityTime().toInstant().atZone(ZoneId.of("Asia/Shanghai")).
                                toLocalDate()) < 0)
                .forEach(activities::add);
        return activities;
    }

    @Override
    public List<Activity> findAllFinishedActivity() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().stream()
                // if the time now is greater than the activity time
                .filter(activity -> LocalDate.now().compareTo
                        (activity.getActivityTime().toInstant().atZone(ZoneId.of("Asia/Shanghai")).
                                toLocalDate()) > 0)
                .forEach(activities::add);
        return activities;
    }

    @Override
    public List<Activity> findAllFreshActivity() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().stream()
                // if the time now is less than the signUpDeadline
                .filter(activity -> LocalDate.now().compareTo
                        (activity.getActivitySignUpDeadline().toInstant().atZone(ZoneId.of("Asia/Shanghai")).
                                toLocalDate()) < 0)
                .forEach(activities::add);
        return activities;
    }

    @Override
    public Activity findActivityById(Integer activityId) {
        return activityRepository.findByActivityId(activityId);
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        Integer activityId = activity.getActivityId();
        Activity oldActivity = activityRepository.findByActivityId(activityId);
        modelMapper.map(activity, oldActivity);
        return activityRepository.save(oldActivity);
    }

    @Override
    public void deleteActivityById(Integer activityId) {
        activityRepository.deleteById(activityId);
    }

    @Override
    public Set<Recruit> findAllRecruitOfActivity(Integer activityId) {
        return recruitRepository.findAllByActivity_ActivityId(activityId);
    }

    @Override
    public Set<Tag> findAllTagOfActivity(Integer activityId) {

        Activity activity = activityRepository.findByActivityId(activityId);
        return  activity.getActivityTags();
    }

    @Override
    public void bindTagToActivity(Integer activityId, Integer tagId) {
        //TODO:(插入前要先确保关联表里面没有该条关联数据)
        Activity activity = activityRepository.findByActivityId(activityId);
        Tag tag = tagRepository.findByTagId(tagId);
        if(activity != null && tag != null){
            activity.getActivityTags().add(tag);
            activityRepository.save(activity);
        }

    }

    @Override
    public void unBindTagToActivity(Integer activityId, Integer tagId) {

    }
}
