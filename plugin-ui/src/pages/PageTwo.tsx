import React from 'react';
import {testIds} from '../components/testIds';
import {PluginPage} from '@grafana/runtime';

function PageTwo() {
  return (
    <PluginPage>
      <div data-testid={testIds.pageTwo.container}>
          <img src="https://www.drivehui.com/wp-content/uploads/2020/04/HUI_LOGO_SOLID.png"/>
      </div>
    </PluginPage>
  );
}

export default PageTwo;
