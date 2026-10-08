import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {Project3DatabaseDataSource} from '../datasources';
import {StoreOwner, StoreOwnerRelations} from '../models';

export class StoreOwnerRepository extends DefaultCrudRepository<
  StoreOwner,
  typeof StoreOwner.prototype.id,
  StoreOwnerRelations
> {
  constructor(
    @inject('datasources.project3database') dataSource: Project3DatabaseDataSource,
  ) {
    super(StoreOwner, dataSource);
  }
}
