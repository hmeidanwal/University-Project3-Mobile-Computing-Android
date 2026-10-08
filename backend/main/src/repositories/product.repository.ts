import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {Project3DatabaseDataSource} from '../datasources';
import {Product, ProductRelations} from '../models';

export class ProductRepository extends DefaultCrudRepository<
  Product,
  typeof Product.prototype.id,
  ProductRelations
> {
  constructor(
    @inject('datasources.project3database') dataSource: Project3DatabaseDataSource,
  ) {
    super(Product, dataSource);
  }
}
