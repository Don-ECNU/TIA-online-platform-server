package com.scsse.workflow.service;

/**
 * @author Alfred Fu
 * Created on 2019-02-21 18:38
 */

public interface GraphService {
    Graph findSimpleGraphById(Integer graphId);

    Graph findWithVectorsById(Integer graphId);
}
