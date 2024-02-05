import {UserType} from "../models/user";

let currentUser: UserType;
const getCurrentUserState = () => {
    return currentUser;
}

const setCurrentUserState = (user: UserType) => {
    currentUser = user;
}

export {
    getCurrentUserState,
    setCurrentUserState
}