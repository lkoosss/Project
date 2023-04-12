//package com.example.sample.zookeeper.controller;
//
//import com.example.common.service.zookeeper.ZookeeperService;
//import com.example.sample.zookeeper.model.ZookeeperRequestModel;
//import com.example.sample.zookeeper.model.ZookeeperResponseModel;
//import org.apache.zookeeper.CreateMode;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@ComponentScan
//public class ZookeeperSampleController {
//
//    @Autowired
//    private ZookeeperService zookeeperService;
//
//    ///////// Zookeeper Test 관리 페이지 /////////
//    @GetMapping("/zookeeper")
//    public ModelAndView zookeeper() {
//        ModelAndView modelAndView = new ModelAndView("/zookeeper/mainControll");
//        return modelAndView;
//    }
//
//    ///////// Zookeeper Znode 조회  /////////
//    @RequestMapping(value = "/zookeeper/selectValue", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel select(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setZnodeValue(this.zookeeperService.selectZnode(znodeKey));
//        return result;
//    }
//
//    ///////// Zookeeper Znode 등록  /////////
//    @RequestMapping(value = "/zookeeper/create", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel create(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        CreateMode znodeType = CreateMode.PERSISTENT;
//        String znodeKey = data.getZnodeKey();
//        String znodeValue = data.getZnodeValue();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setResult(this.zookeeperService.createZnode(znodeType, znodeKey, znodeValue));
//        return result;
//    }
//
//    ///////// Zookeeper Znode 수정  /////////
//    @RequestMapping(value = "/zookeeper/update", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel update(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//        String znodeValue = data.getZnodeValue();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setResult(this.zookeeperService.updateZnode(znodeKey, znodeValue));
//        return result;
//    }
//
//    ///////// Zookeeper Znode 삭제  /////////
//    @RequestMapping(value = "/zookeeper/delete", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel delete(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setResult(this.zookeeperService.deleteZnode(znodeKey));
//        return result;
//    }
//
//    ///////// Zookeeper Znode 속성 조회  /////////
//    @RequestMapping(value = "/zookeeper/selectState", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel selectState(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setZnodeState(this.zookeeperService.selectZnodeState(znodeKey));
//        return result;
//    }
//
//    ///////// Zookeeper Znode 하위 Znode 조회  /////////
//    @RequestMapping(value = "/zookeeper/selectChild", method = {RequestMethod.POST, RequestMethod.GET})
//    public ZookeeperResponseModel selectChild(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//
//        ZookeeperResponseModel result = new ZookeeperResponseModel();
//        result.setZnodeChild(this.zookeeperService.selectZnodeChildren(znodeKey));
//        return result;
//    }
//
//    ///////// Zookeeper Znode Watcher 등록 /////////
//    @RequestMapping(value = "/zookeeper/setWatcher", method = {RequestMethod.POST, RequestMethod.GET})
//    public void setWatcher(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//        this.zookeeperService.setWatcher(znodeKey);
//    }
//
//    ///////// Zookeeper Znode Watcher 해제 /////////
//    @RequestMapping(value = "/zookeeper/unsetWatcher", method = {RequestMethod.POST, RequestMethod.GET})
//    public void unsetWatcher(HttpServletRequest request, @RequestBody ZookeeperRequestModel data) {
//        String znodeKey = data.getZnodeKey();
//        this.zookeeperService.unsetWatcher(znodeKey);
//    }
//
//}
