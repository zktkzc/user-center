import myAxios from "../plugins/myAxios.ts";
import {getCurrentUserState, setCurrentUserState} from "../states/user.ts";

const getCurrentUser = async () => {
    const currentUser = getCurrentUserState();
    if (!currentUser) {
        const res = await myAxios.get('/user/current');
        if (res.data.code === 0 && res.data.data)
            setCurrentUserState(res.data.data);
        return res.data.data;
    }
    return currentUser;
}

export {
    getCurrentUser
}