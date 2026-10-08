import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {Project3DatabaseDataSource} from '../datasources';
import {Account, AccountRelations} from '../models';

export class AccountRepository extends DefaultCrudRepository<
  Account,
  typeof Account.prototype.email,
  AccountRelations
> {
  constructor(
    @inject('datasources.project3database') dataSource: Project3DatabaseDataSource,
  ) {
    super(Account, dataSource);
  }
}
