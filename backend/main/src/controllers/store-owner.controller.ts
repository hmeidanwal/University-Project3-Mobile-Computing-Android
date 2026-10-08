import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  post,
  param,
  get,
  getModelSchemaRef,
  patch,
  put,
  del,
  requestBody,
  response,
} from '@loopback/rest';
import {StoreOwner} from '../models';
import {StoreOwnerRepository} from '../repositories';

export class StoreOwnerController {
  constructor(
    @repository(StoreOwnerRepository)
    public storeOwnerRepository : StoreOwnerRepository,
  ) {}

  @post('/store-owners')
  @response(200, {
    description: 'StoreOwner model instance',
    content: {'application/json': {schema: getModelSchemaRef(StoreOwner)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StoreOwner, {
            title: 'NewStoreOwner',
            
          }),
        },
      },
    })
    storeOwner: StoreOwner,
  ): Promise<StoreOwner> {
    return this.storeOwnerRepository.create(storeOwner);
  }

  @get('/store-owners/count')
  @response(200, {
    description: 'StoreOwner model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(StoreOwner) where?: Where<StoreOwner>,
  ): Promise<Count> {
    return this.storeOwnerRepository.count(where);
  }

  @get('/store-owners')
  @response(200, {
    description: 'Array of StoreOwner model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(StoreOwner, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(StoreOwner) filter?: Filter<StoreOwner>,
  ): Promise<StoreOwner[]> {
    return this.storeOwnerRepository.find(filter);
  }

  @patch('/store-owners')
  @response(200, {
    description: 'StoreOwner PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StoreOwner, {partial: true}),
        },
      },
    })
    storeOwner: StoreOwner,
    @param.where(StoreOwner) where?: Where<StoreOwner>,
  ): Promise<Count> {
    return this.storeOwnerRepository.updateAll(storeOwner, where);
  }

  @get('/store-owners/{id}')
  @response(200, {
    description: 'StoreOwner model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(StoreOwner, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(StoreOwner, {exclude: 'where'}) filter?: FilterExcludingWhere<StoreOwner>
  ): Promise<StoreOwner> {
    return this.storeOwnerRepository.findById(id, filter);
  }

  @patch('/store-owners/{id}')
  @response(204, {
    description: 'StoreOwner PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StoreOwner, {partial: true}),
        },
      },
    })
    storeOwner: StoreOwner,
  ): Promise<void> {
    await this.storeOwnerRepository.updateById(id, storeOwner);
  }

  @put('/store-owners/{id}')
  @response(204, {
    description: 'StoreOwner PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() storeOwner: StoreOwner,
  ): Promise<void> {
    await this.storeOwnerRepository.replaceById(id, storeOwner);
  }

  @del('/store-owners/{id}')
  @response(204, {
    description: 'StoreOwner DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.storeOwnerRepository.deleteById(id);
  }
}
