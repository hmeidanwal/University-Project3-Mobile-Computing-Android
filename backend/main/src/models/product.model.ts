import {belongsTo, Entity, model, property} from '@loopback/repository';
import {StoreOwner, StoreOwnerWithRelations} from '.';

@model({settings: {strict: false}})
export class Product extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    required: true,
  })
  name: string;

  @property({
    type: 'string',
  })
  image?: string;

  // CHANGED THE PRICE TYPE TO DECIMAL
  @property({
    type: 'number',
    required: true,
    postgresql: {
      columnName: 'price',
      dataType: 'decimal',
      dataPrecision: 10,
      dataScale: 2,
      nullable: 'NO',
    },
  })
  price: number;

  @property({
    type: 'string',
    required: true,
  })
  amount: string;

  @property({
    type: 'string',
    required: true,
  })
  unit: string;

  @belongsTo(() => StoreOwner, {name: 'storeOwner', keyTo: 'store'})
  storeId: number;

  // Define well-known properties here

  // Indexer property to allow additional data
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;

  constructor(data?: Partial<Product>) {
    super(data);
  }
}

export interface ProductRelations {
  storeOwner?: StoreOwnerWithRelations;
}

export type ProductWithRelations = Product & ProductRelations;
