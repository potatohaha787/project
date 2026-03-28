// 路由表
const constantRouterMap = [
  // ************* 前台路由 **************
  {
    path: '/',
    redirect: '/index'
  },
  {
    path: '/index',
    name: 'index',
    redirect: '/index/portal',
    component: () => import('/@/views/index/index.vue'),
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('/@/views/index/login.vue')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import('/@/views/index/register.vue')
      },
      {
        path: 'portal',
        name: 'portal',
        component: () => import('/@/views/index/portal.vue')
      },
      {
        path: 'history',
        name: 'history',
        component: () => import('/@/views/index/history.vue')
      },
      {
        path: 'foodDetail',
        name: 'foodDetail',
        component: () => import('/@/views/index/foodDetail.vue')
      },
      {
        path: 'celebrity',
        name: 'celebrity',
        component: () => import('/@/views/index/celebrity.vue')
      },
      {
        path: 'food',
        name: 'food',
        component: () => import('/@/views/index/food.vue')
      },
      {
        path: 'heritage',
        name: 'heritage',
        component: () => import('/@/views/index/heritage.vue')
      },
      {
        path: 'heritageDetail',
        name: 'heritageDetail',
        component: () => import('/@/views/index/heritageDetail.vue')
      },
      {
        path: 'celebrity/:id',
        name: 'celebrityDetail',
        component: () => import('/@/views/index/celebrity-detail.vue')
      },
      {
        path: 'scenic',
        name: 'scenic',
        component: () => import('/@/views/index/scenic.vue')
      },
      {
        path: 'detail',
        name: 'detail',
        component: () => import('/@/views/index/detail.vue')
      },
      {
        path: 'guide',
        name: 'guide',
        component: () => import('/@/views/index/guide/guide.vue')
      },
      {
        path: 'confirm',
        name: 'confirm',
        component: () => import('/@/views/index/confirm.vue')
      },
      {
        path: 'pay',
        name: 'pay',
        component: () => import('/@/views/index/pay.vue')
      },
      {
        path: 'publishGuide',
        name: 'publishGuide',
        component: () => import('/@/views/index/guide/PublishGuide.vue'),
      },
      {
        path: 'GuideDetail',
        name: 'GuideDetail',
        component: () => import('/@/views/index/guide/GuideDetail.vue'),
      },
      {
        path: 'ForumDetail',
        name: 'ForumDetail',
        component: () => import('/@/views/index/guide/ForumDetail.vue'),
      },
      {
        path: 'ForumList',
        name: 'ForumList',
        component: () => import('/@/views/index/guide/ForumList.vue'),
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('/@/views/index/search.vue')
      },
      {
        path: 'usercenter',
        name: 'usercenter',
        redirect: '/index/usercenter/addressView',
        component: () => import('/@/views/index/usercenter.vue'),
        children: [
          {
            path: 'wishThingView',
            name: 'wishThingView',
            component: () => import('/@/views/index/user/wish-thing-view.vue')
          },
          {
            path: 'collectThingView',
            name: 'collectThingView',
            component: () => import('/@/views/index/user/collect-thing-view.vue')
          },
          {
            path: 'myPostView',
            name: 'myPostView',
            component: () => import('/@/views/index/user/myPostView.vue')
          },

          {
            path: 'userInfoEditView',
            name: 'userInfoEditView',
            component: () => import('/@/views/index/user/userinfo-edit-view.vue')
          },
          {
            path: 'followView',
            name: 'followView',
            component: () => import('/@/views/index/user/follow-view.vue')
          },
          {
            path: 'fansView',
            name: 'fansView',
            component: () => import('/@/views/index/user/fans-view.vue')
          },
          {
            path: 'commentView',
            name: 'commentView',
            component: () => import('/@/views/index/user/comment-view.vue')
          },
          {
            path: 'securityView',
            name: 'securityView',
            component: () => import('/@/views/index/user/security-view.vue')
          },
          {
            path: 'messageView',
            name: 'messageView',
            component: () => import('/@/views/index/user/message-view.vue')
          },
        ]
      }
    ]
  },
  {
    path: '/adminLogin',
    name: 'adminLogin',
    component: () => import('/@/views/admin/admin-login.vue'),
  },
  {
    path: '/admin',
    name: 'admin',
    redirect: '/admin/thing',
    component: () => import('/@/views/admin/main.vue'),
    children: [
      { path: 'overview', name: 'overview', component: () => import('/@/views/admin/overview.vue') },
      { path: 'adminCelebrity', name: 'adminCelebrity', component: () => import('/@/views/admin/celebrity.vue') },
      { path: 'adminHistory', name: 'adminHistory', component: () => import('/@/views/admin/adminHistory.vue') },
      { path: 'thing', name: 'thing', component: () => import('/@/views/admin/thing.vue') },
      { path: 'comment', name: 'comment', component: () => import('/@/views/admin/comment.vue') },
      { path: 'user', name: 'user', component: () => import('/@/views/admin/user.vue') },
      { path: 'classification', name: 'classification', component: () => import('/@/views/admin/classification.vue') },
      { path: 'adminHeritage', name: 'adminHeritage', component: () => import('/@/views/admin/adminHeritage.vue') },
      { path: 'adminFood', name: 'adminFood', component: () => import('/@/views/admin/adminFood.vue') },
      { path: 'ad', name: 'ad', component: () => import('/@/views/admin/ad.vue') },
      { path: 'adminPost', name: 'adminPost', component: () => import('/@/views/admin/adminPost.vue') },
      { path: 'notice', name: 'notice', component: () => import('/@/views/admin/notice.vue') },
      { path: 'loginLog', name: 'loginLog', component: () => import('/@/views/admin/login-log.vue') },
      { path: 'opLog', name: 'opLog', component: () => import('/@/views/admin/op-log.vue') },
      { path: 'errorLog', name: 'errorLog', component: () => import('/@/views/admin/error-log.vue') },
      { path: 'sysInfo', name: 'sysInfo', component: () => import('/@/views/admin/sys-info.vue') },
    ]
  },
];

export default constantRouterMap;
