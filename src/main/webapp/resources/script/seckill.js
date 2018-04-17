//存放主要交互逻辑js代码
//js模块化
var seckill = {
    //封装秒杀相关ajax的url
    URL : {
        now : function(){
            return '/seckill/time/now';
        },
        exposer : function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution : function(seckillId,md5){
            return '/seckill/'+ seckillId + '/' + md5 + '/execution';
        }

    },
    handleSeckill :function(seckillId,node){
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function(result){
            //在回调函数中，执行交互流程
            if(result && result.success){
                var exposer = result.data;
                if(exposer.exposed){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer.md5;
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log("killUrl:" + killUrl);
                    //绑定一次点击事件
                    $("#killBtn").one('click',function(){
                        //执行秒杀请求
                        //禁用按钮
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        $.post(killUrl,{},function(result){
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');

                            }
                        })
                    });
                    node.show();
                }else{
                    //未开启秒杀
                    var now = exposer.now;
                    var start = exposer.start;
                    var end  = exposer.end;
                    //重新进入计时逻辑
                    seckill.countdown(seckillId, now , start, end);

                }

            }else{
                console.log('result:'+result)
            }

        })
    },
    validatePhone: function(phone){
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },

    countdown:function(seckillId,nowTime,startTime,endTime){ 
        var seckillBox = $('#seckill-box'); 
        //时间判断 
        if(nowTime > endTime){ 
            seckillBox.html('秒杀结束！'); 
        }else if(nowTime < startTime ){ 
            //秒杀未开始，计时事件绑定 
            var killTime = new Date(startTime + 1000); 
            seckillBox.countdown(killTime, function(event){ 
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒'); 
                seckillBox.html(format); 
            }).on('finish.countdown', function(){ //时间完成后回调事件 
                // 获取秒杀地址，控制逻辑 
               seckill.handleSeckill(seckillId,seckillBox); 
            }); 
        }else { 
            //秒杀开始 
            seckill.handleSeckill(seckillId,seckillBox);
        } 
    },


    //详情页秒杀逻辑
    detail : {
        // 详情页初始化
        init : function(params){
            //手机验证和登录，计时交互
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone'); //string类型

            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({
                    show:true,
                    backdrop:'static',//禁止位置关闭
                    keyboard:false
                });
                $("#killPhoneBtn").click(function(){

                    var inputPhone = $('#killPhoneKey').val();

                    if(seckill.validatePhone(inputPhone)){
                        //电话写入cookie
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'})//创建一个cookie并设置有效时间为 7天,并只在／seckill路径下才生效
                        //刷新页面
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(),{},function(result){
                if(result && result["success"]){
                    var nowTime = result['data'];
                    seckill.countdown(seckillId, nowTime, startTime,endTime);
                }else{
                    console.info('result:'+result)
                }
            });
        }


    }
}