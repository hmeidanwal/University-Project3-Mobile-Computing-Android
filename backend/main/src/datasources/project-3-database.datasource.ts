import {inject, lifeCycleObserver, LifeCycleObserver} from '@loopback/core';
import {juggler} from '@loopback/repository';

const config = {
  name: 'project3database',
  connector: 'postgresql',
  url: process.env.DATABASE_URL,

  // LoopBack sometimes needs SSL options inside "settings"
  settings: {
    ssl: {
      rejectUnauthorized: false,
    },
  },

  // and some versions also read "ssl" at top-level
  ssl: {
    rejectUnauthorized: false,
  },
};

@lifeCycleObserver('datasource')
export class Project3DatabaseDataSource extends juggler.DataSource
  implements LifeCycleObserver {
  static dataSourceName = 'project3database';
  static readonly defaultConfig = config;

  constructor(
    @inject('datasources.config.project3database', {optional: true})
    dsConfig: object = config,
  ) {
    super(dsConfig);

    // TEMP debug: shows real DB error instead of just "AggregateError"
    this.on('error', (err: any) => {
      console.error('Datasource error details:', err);
      if (err?.errors) console.error('Inner errors:', err.errors);
    });
  }
}
