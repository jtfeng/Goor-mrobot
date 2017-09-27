package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.fixpath.service.impl.FixPathServiceImpl;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class RoadPathController {

    private static final Logger log = LoggerFactory.getLogger(RoadPathController.class);

    @Autowired
    private RoadPathService roadPathService;

    /**
     * 创建
     * @param body
     * @return
     */
    @PostMapping("/asset/roadPath")
    public AjaxResult createRoadPath(@RequestBody Map<String, Object> body){
        // 拟合方式现假定有三种 （1：直线；2：曲线；3.云端定义）
        // String -> pathName
        // String -> pattern
        // String -> data
        // List<Long> -> points -> 前端传递格式为 一个数组
        // Long cloudSceneId
        try {
            roadPathService.createRoadPath(body);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 更新
     * @param body
     * @return
     */
    @RequestMapping(value = "/asset/roadPath", method = RequestMethod.PUT)
    public AjaxResult updateRoadPath(@RequestBody Map<String, Object> body){
        try {
            roadPathService.updateRoadPath(body);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除指定的道路路径编号
     * @param id
     * @return
     */
    @ RequestMapping(value = "/asset/roadPath/{id}", method = RequestMethod.DELETE)
    public AjaxResult deleteRoadPath(@PathVariable("id") String id){
        try {
            this.roadPathService.deleteById(Long.parseLong(id));
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 分页列表查询路径信息
     * @param whereRequest
     * @return
     */
    @GetMapping("/asset/roadPath")
    public AjaxResult roadPathList(WhereRequest whereRequest) {
        try {
            List<RoadPath> list = roadPathService.listRoadPaths(whereRequest);
            PageInfo<RoadPath> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询成功");
        } catch (Exception e) {
            return AjaxResult.failed(e,         "查询失败");
        }
    }

    /**
     * 根据 开始点 和 结束点 查询路径
     * @return
     */
    @GetMapping("/asset/roadPath/{startPoint}/{endPoint}/{sceneName}/{mapName}")
    public AjaxResult findRoadPathByStartAndEndPoint(
            @PathVariable("startPoint") Long startPoint,
            @PathVariable("endPoint") Long endPoint,
            @PathVariable("sceneName") String sceneName,
            @PathVariable("mapName") String mapName){
        try {
            List<RoadPathDetail> roadPathDetails = this.roadPathService.findRoadPathByStartAndEndPoint(startPoint, endPoint, sceneName, mapName);
            return AjaxResult.success(roadPathDetails);
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }


    private static final int PATH_TYPE_IC = 1; //工控上传路径id
    private static final String PATH = "path"; //RoadPath name前缀
    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;
    @Autowired
    private PointService pointService;
    /**
     * 读取工控路径文件并入库
     * @return
     */
    @RequestMapping(value = "/services/roadPath/readFile", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult deleteRoadPath(@RequestParam(value = "resFile") MultipartFile file){
        try {
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + "roadPath" + File.separator + DateTimeUtils.getShortDateTime());

            if (!file.isEmpty()) {
                dest.mkdirs();
                String fileName = file.getOriginalFilename();
                final File dest1 = FileUtils.getFile(dest.getPath() + File.separator + fileName);

                if (!dest1.exists()) {
                    dest1.createNewFile();
                }
                file.transferTo(dest1);


                //异步插入数据库
                //用线程池代替原来的new Thread方法
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader br = null;
                        try {
                            br = new BufferedReader(new InputStreamReader(new FileInputStream(dest1), "UTF-8"));
                            String temp = br.readLine();
                            String messageData = temp == null? null : temp.trim();
                            while (temp != null) {
                                temp = br.readLine();
                                if(temp != null) {
                                    messageData += temp.trim();
                                }
                            }


                            if (StringUtil.isNullOrEmpty(messageData))
                                return;

                            JSONObject jsonObject = JSON.parseObject(messageData);
                            String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
                            String paths = jsonObject.getString(TopicConstants.PATHS);
                            List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);
                            for (PathDTO pathDTO : pathDTOList) {
                                MapPoint startPoint = FixPathServiceImpl.findOrSaveMapPointByPath(sceneName, pathDTO, true,pointService);
                                MapPoint endPoint = FixPathServiceImpl.findOrSaveMapPointByPath(sceneName, pathDTO, false,pointService);
                                //封装RoadPath对象，保存数据库
                                RoadPath roadPath = new RoadPath();
                                roadPath.setSceneName(sceneName);
                                roadPath.setMapName(pathDTO.getStartMap());
                                roadPath.setPathId(pathDTO.getId() + "");
                                //添加roadpath查询，根据场景，地图，pathid进行查询，如果存在，则更新，不存在则添加
                                RoadPath roadPathDB = roadPathService.findRoadPath(roadPath);
                                //继续封装参数
                                roadPath.setStartPoint(startPoint.getId());
                                roadPath.setEndPoint(endPoint.getId());
                                roadPath.setPathType(PATH_TYPE_IC);
                                roadPath.setPathName(PATH + pathDTO.getId());
                                roadPath.setCreateTime(new Date());
                                roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                                roadPath.setWeight(1L);
                                //根据数据库查询结果判断是更新还是新增
                                if (null != roadPathDB) {
                                    roadPath.setId(roadPathDB.getId());
                                    roadPathService.update(roadPath);  //更新
                                } else {
                                    roadPathService.save(roadPath);  //新增
                                }
                            }

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        finally {
                            try {
                                if (br != null) {
                                    br.close();
                                }
                            }
                            catch (Exception e1) {
                                log.error(e1.getMessage(), e1);
                            }
                        }
                    }

                }).start();
            }
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }



}