import {belongsTo, Entity, model, property} from '@loopback/repository';
import {Product, ProductWithRelations} from '.';

@model({settings: {strict: false}})
export class Discount extends Entity {

  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
    required: true,
  })
  price: number;

  @property({
    type: 'date',
    required: true,
  })
  start_date: string;

  @property({
    type: 'date',
    required: true,
  })
  end_date: string;

  @belongsTo(() => Product, {name: 'product', keyTo: 'id'})
  product_id: number;

  // Define well-known properties here

  // Indexer property to allow additional data
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;

  constructor(data?: Partial<Discount>) {
    super(data);
  }
}

export interface DiscountRelations {
  product?: ProductWithRelations;
}

export type DiscountWithRelations = Discount & DiscountRelations;
