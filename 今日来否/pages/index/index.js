var app = getApp()
Page({
  data: {
    users: {
      '赵钱孙': [0, 0, 1, 0, 0],
      '李周五': [1, 0, 1, 2, 0],
      '郑王': [1, 1, 2, 0, 0]
    },
    userInfo: null,
  },
  connectTryTimes: 0, //连接尝试次数
  SEAT_COUNT: 6,
  checkNet: function () {
    var that = this;
    if (that.connectTryTimes % 10 == 3) {
      wx.showToast({
        title: '请检查网络'
      })
    }
    wx.connectSocket({
      url: 'wss://weiyinfu.cn/wss/come/chat',
    })
    wx.onSocketOpen(function (res) {
      console.log('websocket opend')
      console.log('WebSocket连接已打开！')
      that.connectTryTimes = 0
      wx.sendSocketMessage({
        data: JSON.stringify({
          event: 'register',
          openid: app.userInfo.nickName,
          nick: app.userInfo.nickName
        })
      })
    })
    wx.onSocketError(res => {
      that.data.connectTryTimes++
      console.log('WebSocket连接打开失败，请检查！')
    })
    wx.onSocketMessage(res => {
      console.log('收到服务器内容：' + res.data)
      this.setData({ users: this.tongji(JSON.parse(res.data)) })
    })
    wx.onSocketClose(function (res) {
      console.log('websocket close')
      if (that.connectTryTimes > 2) {
        setTimeout(that.checkNet, 2000)
      }
      else {
        that.checkNet()
      }
    })
  },
  onLoad: function () {
    console.log('onLoad')
    wx.request({
      url: 'https://weiyinfu.cn/tomcat/come/state',
      success: res => {
        this.setData({
          users: this.tongji(res.data)
        })
        console.log('checkNet')
      }
    })
    if (app.userInfo) {
      this.checkNet()
    }
    else {
      app.userInfoCallback = this.checkNet
    }
  },
  //如果某一天人数爆满，则将这一天的人come值置为2
  tongji: function (a) {
    for (var i = 0; i < 5; i++) {
      var people = 0
      for (var j = 0; j < a.length; j++) {
        if (a[j].days[i]) {
          people++
          a[j].days[i] = 1//都置成1
        }
      }
      if (people > this.SEAT_COUNT) {
        for (var j = 0; j < a.length; j++) {
          if (a[j].days[i]) {
            a[j].days[i] = 2
          }
        }
      }
    }
    return a
  },
  clk: function (event) {
    var dataset = event.target.dataset;
    if (!dataset.day) return false
    console.log("clk" + dataset.day)
    wx.sendSocketMessage({
      data: JSON.stringify({
        event: 'click',
        day: dataset.day,
        openid: app.userInfo.nickName,
        come: dataset.come
      })
    })
  }
})