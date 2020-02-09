package com.scsse.workflow.service.impl;

import com.scsse.workflow.constant.ErrorMessage;
import com.scsse.workflow.entity.dto.TeamDto;
import com.scsse.workflow.entity.dto.UserDto;
import com.scsse.workflow.entity.model.Team;
import com.scsse.workflow.entity.model.User;
import com.scsse.workflow.handler.WrongUsageException;
import com.scsse.workflow.repository.TeamRepository;
import com.scsse.workflow.service.TeamService;
import com.scsse.workflow.util.dao.DtoTransferHelper;
import com.scsse.workflow.util.dao.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Alfred Fu
 * Created on 2019/10/6 6:52 下午
 */

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final DtoTransferHelper dtoTransferHelper;

    private final UserUtil userUtil;

    private final ModelMapper modelMapper;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, DtoTransferHelper dtoTransferHelper,
        UserUtil userUtil, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.dtoTransferHelper = dtoTransferHelper;
        this.userUtil = userUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public TeamDto getTeam(Integer teamId) {
        Optional<Team> result = teamRepository.findById(teamId);
        return result.map(dtoTransferHelper::transferToTeamDto).orElse(null);
    }

    @Override
    public TeamDto createTeam(Team team) {
        User loginUser = userUtil.getLoginUser();
        team.setLeader(loginUser);
        team.getMembers().add(loginUser);
        return dtoTransferHelper.transferToTeamDto(teamRepository.save(team));
    }

    @Override
    public TeamDto updateTeam(Team team) throws Exception {
        Optional<Team> result = teamRepository.findById(team.getId());
        if (result.isPresent()) {
            Team oldTeam = result.get();
            modelMapper.map(team, oldTeam);
            return dtoTransferHelper.transferToTeamDto(teamRepository.save(oldTeam));
        } else {
            throw new WrongUsageException(ErrorMessage.UPDATE_ENTITY_NOT_FOUND);
        }
    }

    @Override
    public void deleteTeam(Integer teamId) {
        teamRepository.deleteById(teamId);
    }

    @Override
    public List<UserDto> getTeamMembers(Integer teamId) {
        Optional<Team> result = teamRepository.findById(teamId);
        if (result.isPresent()) {
            return dtoTransferHelper.transferToListDto(
                    result.get().getMembers(), user -> dtoTransferHelper.transferToUserDto((User) user)
            );
        } else {
            return new ArrayList<>();
        }
    }
}
