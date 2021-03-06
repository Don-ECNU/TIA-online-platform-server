package com.scsse.workflow.service.impl;

import com.scsse.workflow.entity.dto.RecruitDto;
import com.scsse.workflow.entity.dto.UserAppliedRecruit;
import com.scsse.workflow.entity.dto.UserDto;
import com.scsse.workflow.entity.model.Recruit;
import com.scsse.workflow.entity.model.Tag;
import com.scsse.workflow.entity.model.Team;
import com.scsse.workflow.entity.model.User;
import com.scsse.workflow.handler.WrongUsageException;
import com.scsse.workflow.repository.RecruitRepository;
import com.scsse.workflow.repository.TagRepository;
import com.scsse.workflow.repository.TeamRepository;
import com.scsse.workflow.service.RecruitService;
import com.scsse.workflow.util.container.Pair;
import com.scsse.workflow.util.dao.DtoTransferHelper;
import com.scsse.workflow.util.dao.UserUtil;
import com.scsse.workflow.util.mvc.PredicateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Alfred Fu
 * Created on 2019-02-19 20:18
 */
@Service
@Transactional
public class RecruitServiceImpl implements RecruitService {

    private final ModelMapper modelMapper;

    private final DtoTransferHelper dtoTransferHelper;

    private final RecruitRepository recruitRepository;

    private final TagRepository tagRepository;

    private final TeamRepository teamRepository;

    private final UserUtil userUtil;


    @Autowired
    public RecruitServiceImpl(ModelMapper modelMapper, DtoTransferHelper dtoTransferHelper, RecruitRepository recruitRepository, TagRepository tagRepository, TeamRepository teamRepository, UserUtil userUtil) {
        this.modelMapper = modelMapper;
        this.dtoTransferHelper = dtoTransferHelper;
        this.recruitRepository = recruitRepository;
        this.tagRepository = tagRepository;
        this.teamRepository = teamRepository;
        this.userUtil = userUtil;
    }

    @Override
    public List<RecruitDto> findPaginationRecruit(Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize, Sort.Direction.DESC, "creatTime");
        List<RecruitDto> result = new ArrayList<>();
        User currentUser = userUtil.getLoginUser();
        recruitRepository.findAll(pageable).map(
                o -> dtoTransferHelper.transferToRecruitDto(o, currentUser)
        ).forEach(result::add);
        return result;
    }

    @Override
    public List<RecruitDto> findPaginationRecruitWithCriteria(Integer pageNum, Integer pageSize, HashMap<Integer, Pair<String, String>> queryParam) {
        Pageable pageable = new PageRequest(pageNum, pageSize, Sort.Direction.DESC, "createTime");
        List<RecruitDto> result = new ArrayList<>();
        User currentUser = userUtil.getLoginUser();
        recruitRepository.findAll((Specification<Recruit>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            PredicateUtil predicateHelper = new PredicateUtil<>(criteriaBuilder, root);
            queryParam.forEach(
                    (predicateType, KV) -> predicateList.add(predicateHelper.generatePredicate(predicateType, KV.getKey(), KV.getValue()))
            );
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }, pageable).map(
                o -> dtoTransferHelper.transferToRecruitDto(o, currentUser)
        ).forEach(result::add);
        return result;
    }

    @Override
    public RecruitDto findRecruitById(Integer recruitId) {
        User u = userUtil.getLoginUser();
        return dtoTransferHelper.transferToRecruitDto(recruitRepository.findOne(recruitId), u);
    }

    @Override
    public RecruitDto createRecruit(Recruit recruit) {
        return dtoTransferHelper.transferToRecruitDto(recruitRepository.save(recruit), userUtil.getLoginUser());
    }

    @Override
    public RecruitDto updateRecruit(Recruit recruit) {
        Integer recruitId = recruit.getId();
        Recruit oldRecruit = recruitRepository.findOne(recruitId);
        modelMapper.map(recruit, oldRecruit);
        return dtoTransferHelper.transferToRecruitDto(recruitRepository.save(oldRecruit), userUtil.getLoginUser());
    }

    @Override
    public void deleteRecruitById(Integer recruitId) {
        recruitRepository.delete(recruitId);
    }

    @Override
    public void applyOneRecruit(Integer userId, Integer recruitId) throws WrongUsageException {
        Recruit recruit = recruitRepository.findOne(recruitId);
        User user = userUtil.getUserByUserId(userId);
        if(user!=null&&recruit!=null) {
            user.getApplyRecruits().add(recruit);
            userUtil.saveUser(user);
        }
    }

    @Override
    public void cancelAppliedRecruit(Integer userId, Integer recruitId) throws WrongUsageException {
        Recruit recruit = getOne(recruitId);
        User user = userUtil.getUserByUserId(userId);
        if(user!=null&&recruit!=null) {
            user.getApplyRecruits().remove(recruit);
            userUtil.saveUser(user);
        }
    }

    @Override
    public void addMember(Integer userId, Integer recruitId) throws WrongUsageException {
        Recruit recruit = recruitRepository.findOne(recruitId);
        User user = userUtil.getUserByUserId(userId);
        if (recruit != null && user != null) {
            recruit.setRecruitRegisteredNumber(recruit.getRecruitWillingNumber()+1);
            recruit.getParticipants().add(user);
            user.getApplyRecruits().remove(recruit);
            recruitRepository.save(recruit);
        }
    }

    @Override
    public void removeMember(Integer userId, Integer recruitId) throws WrongUsageException {
        Recruit recruit = recruitRepository.findOne(recruitId);
        User user = userUtil.getUserByUserId(userId);

        if (recruit != null && user != null) {
            recruit.setRecruitRegisteredNumber(recruit.getRecruitWillingNumber()-1);
            recruit.getParticipants().remove(user);
            user.getApplyRecruits().add(recruit);
            recruitRepository.save(recruit);
        }
    }


    @Override
    public List<UserDto> findAllMemberOfRecruit(Integer recruitId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        return dtoTransferHelper.transferToListDto(recruit.getParticipants(), eachItem -> dtoTransferHelper.transferToUserDto((User) eachItem));
    }

    @Override
    public List<UserDto> findAllFollowerOfRecruit(Integer recruitId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        return dtoTransferHelper.transferToListDto(recruit.getFollowers(), eachItem -> dtoTransferHelper.transferToUserDto((User) eachItem));
    }

    @Override
    public List<UserDto> findAllApplicantOfRecruit(Integer recruitId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        return dtoTransferHelper.transferToListDto(recruit.getApplicants(), eachItem -> dtoTransferHelper.transferToUserDto((User) eachItem));


    }

    @Override
    public Set<Tag> findAllTagOfRecruit(Integer recruitId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        return recruit.getRecruitTags();
    }

    @Override
    public void bindTagToRecruit(Integer recruitId, Integer tagId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        Tag tag = tagRepository.findByTagId(tagId);
        if (recruit != null && tag != null && !recruit.getRecruitTags().contains(tag)) {
            recruit.getRecruitTags().add(tag);
            recruitRepository.save(recruit);
        }
    }

    @Override
    public void unBindTagToRecruit(Integer recruitId, Integer tagId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        Tag tag = tagRepository.findByTagId(tagId);
        if (recruit != null && tag != null) {
            recruit.getRecruitTags().remove(tag);
            recruitRepository.save(recruit);
        }
    }

    @Override
    public void finishRecruit(Integer recruitId) {
        Recruit recruit = recruitRepository.findOne(recruitId);
        recruit.setRecruitState(finish);

        Team team = recruit.getTeam();
        team.getMembers().addAll(recruit.getParticipants());
        teamRepository.save(team);
    }

    @Override
    public List<UserAppliedRecruit> findUsersAppliedMyRecruits() throws WrongUsageException {
        List<UserAppliedRecruit> result = new ArrayList<>();
        Set<Recruit> recruits = recruitRepository.findAllByCreator_Id(userUtil.getLoginUserId());
        // select all applicant and add it to the result.
        recruits.forEach(
            recruit ->
                result.addAll(dtoTransferHelper.transferToListDto(recruit.getApplicants(), recruit,
                    (firstParam, secondParam) -> dtoTransferHelper
                        .transferToUserAppliedRecruit((User) firstParam, (Recruit) secondParam)
                ))

        );
        return result;
    }
    @Override
    public Recruit findRecruit(Integer recruitId){
        return  recruitRepository.findOne(recruitId);
    }

    @Override
    public Recruit getOne(Integer id) {
        System.out.println("id: " + id);
        Recruit recruit = recruitRepository.findOne(id);
        return recruit;
    }
}
