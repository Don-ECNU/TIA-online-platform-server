package com.scsse.workflow.service.impl;

import com.scsse.workflow.entity.Tag;
import com.scsse.workflow.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alfred Fu
 * Created on 2019-02-19 20:19
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {
    @Override
    public Tag findTagById(Integer tagId) {
        return null;
    }

    @Override
    public void createTag(Tag tag) {

    }

    @Override
    public void updateTag(Tag tag) {

    }

    @Override
    public void deleteTagById(Integer tagId) {

    }
}
