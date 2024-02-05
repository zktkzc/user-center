import IndexPage from "../pages/IndexPage.vue"
import TeamPage from "../pages/TeamPage.vue"
import UserUpdatePage from "../pages/UserUpdatePage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import UserPage from "../pages/UserPage.vue";
import UserTeamCreatePage from "../pages/UserTeamCreatePage.vue";
import UserTeamJoinPage from "../pages/UserTeamJoinPage.vue";

const routes = [
    {path: '/', redirect: '/user/login'},
    {path: '/index', title: '伙伴推荐', component: IndexPage},
    {path: '/team', title: '队伍', component: TeamPage},
    {path: '/user', title: '个人信息', component: UserPage},
    {path: '/user/update', title: '更新个人信息', component: UserUpdatePage},
    {path: '/user/team/create', title: '我创建的队伍', component: UserTeamCreatePage},
    {path: '/user/team/join', title: '我加入的队伍', component: UserTeamJoinPage},
    {path: '/search', title: '找伙伴', component: SearchPage},
    {path: '/user/edit', title: '编辑个人信息', component: UserEditPage},
    {path: '/user/list', title: '搜索结果', component: SearchResultPage},
    {path: '/user/login', component: UserLoginPage},
    {path: '/team/add', title: '创建队伍', component: TeamAddPage},
    {path: '/team/update', title: '更新队伍信息', component: TeamUpdatePage}
]

export default routes