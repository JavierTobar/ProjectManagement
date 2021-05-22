import { RouteConfig } from 'vue-router';

const routes: RouteConfig[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue') },
      { path: '/projects', component: () => import('pages/Projects.vue') },
      { path: '/tasks', component: () => import('pages/Tasks.vue') },
      { path: '/users', component: () => import('pages/Users.vue') },
      { path: '/work-assignment', component: () => import('pages/WorkAssignment.vue') },
      { path: '/todo-list', component: () => import('pages/TodoList.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '*',
    component: () => import('pages/Error404.vue')
  }
];

export default routes;
