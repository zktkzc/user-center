import {PageContainer} from '@ant-design/pro-components';
import '@umijs/max';
import React, {PropsWithChildren} from 'react';

const Admin: React.FC = (props: PropsWithChildren) => {
  const {children} = props;
  return (
    <PageContainer>
      {children}
    </PageContainer>
  );
};
export default Admin;
