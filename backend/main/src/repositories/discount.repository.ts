import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {Project3DatabaseDataSource} from '../datasources';
import {Discount, DiscountRelations} from '../models';

export class DiscountRepository extends DefaultCrudRepository<
  Discount,
  typeof Discount.prototype.product_id,
  DiscountRelations
> {
  constructor(
    @inject('datasources.project3database') dataSource: Project3DatabaseDataSource,
  ) {
    super(Discount, dataSource);
  }
}
