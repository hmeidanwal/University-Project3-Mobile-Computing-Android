import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {Project3DatabaseDataSource} from '../datasources';
import {User, UserRelations} from '../models';

export class UserRepository extends DefaultCrudRepository<
  User,
  typeof User.prototype.id,
  UserRelations
> {
  constructor(
    @inject('datasources.project3database') dataSource: Project3DatabaseDataSource,
  ) {
    super(User, dataSource);
  }
}
