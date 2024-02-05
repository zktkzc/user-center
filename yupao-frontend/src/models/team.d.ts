/**
 * 队伍类别
 */
export type TeamType = {
    id: number;
    description: string,
    expireTime?: Date,
    maxNum: number,
    name: string,
    password?: string,
    status: number,
    createTime: Date,
    updateTime: Date,
    createUser?: UserType,
    hasJoin: boolean,
    members: number
}